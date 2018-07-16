authentication by function (standard mode), two options:
1. in POST URI as  /HttpTrigerCSharp?name=kris&code=<function key here>
2. in Headers  as  "x-functions-key" -->   <function key here>
with Webhook mode:
1. either "default" function key or "_master" admin key
2. for all other keys you need query string:
    ?code=<functio key here>&clientid=client1
3. headers:
    x-functions-clientid     client1
    x-functions-key   <function key here>


///////////////////////////////# WEBHOOK

#r "Newtonsoft.Json"

using System;
using System.Net;
using Newtonsoft.Json;

public class Order {
    public string PartitionKey { get;set; }     // for Azure Table Storage
    public string RowKey { get;set; }           // for Azure Table Storage
    public string OrderId {get;set;}
    public string Product {get;set;}
    public string Email {get;set;}
    public decimal Price {get;set}
}

// WEBHOOK --> MESSAGE IN QUEUE
public static async Task<object> Run(HttpRequestMessage req, TraceWriter log)
{
    log.Info($"Webhook was triggered!");
    string jsonContent = await req.Content.ReadAsStringAsync();
    //dynamic data = JsonConvert.DeserializeObject(jsonContent);
    var order = JsonConvert.DeserializeObject<Order>(jsonContent)       // note CLASS defined
    log.Info($"Order {order.Product} received from {order.Email} with {order.Price}");
    return req.CreateResponse(HttpStatusCode.OK, new {
        message = $"Thank you"
    });
}
// we can output to multiple destinations, help: https://docs.microsoft.com/en-us/azure/azure-functions/functions-bindings-storage-queue
// when function is defined as "ASYNC", then we cannot use "out", so we need another approach
public static async Task<object> Run(HttpRequestMessage req, TraceWriter log, out Order outputQueueItem)  // also out string etc. see help
public static async Task<object> Run(HttpRequestMessage req, TraceWriter log, IAsyncCollector<Order> outputQueueItem,  IAsyncCollector<Order> outputTable)  // also out string etc. see help
{
    (...)
    log.Info($"Order {order.Product} received from {order.Email} with {order.Price}");
    order.PartitionKey = "Orders";
    order.RowKey = order.OrderId;
    
    await outputQueueItem.AddAsync(order);      // upload to queue
    await outputTable.AddAsync(order);          // upload to table
    (...)
}

// STORE TXT FILE (LIC) IN BLOB
// then we can click this another output format, and at the bottom select GO to automatically trigger new hook, choose
// QueueTrigger. We can similalry use output of it, for another binding, to store in e.g. Blob, so choose Output and Azure Blob, then:
#load "../Shared/OrderHelper.csx"  // here we keep common code, e.g. Orders class definition
public static void Run(Order myQueueItem, TrackWriter log, out string outputBlob)
public static void Run(Order myQueueItem, TrackWriter log, TextWriter outputBlob)
public static void Run(Order myQueueItem, TrackWriter log, IBinder binder)  // if we want to change output file name, instead  licenses/{rand-guid}.lic
{
    log.Info($"Received from queue: Order {myQueueItem.OrderId}, Product {myQueueItem.Product}, Email...");
    // addind this to handle binding, to read file name from code instead of typing {rand-guid}
    using (var outputBlob = binder.Bind<TextWriter>(
        new BlobAttribute($"licenses/{myQueueItem.OrderId}.lic")))
    {
        outputBlob.WriteLine($"Order ID: {myQueueItem.OrderId}");
        outputBlob.WriteLine($"Email: {myQueueItem.Email}");
        outputBlob.WriteLine($"Product: {myQueueItem.Product}");
        outputBlob.WriteLine($"Date: {DateTime.UtcNow}");
        var md5 = System.Security.Cryptography.MD5.Create();
        var hash = md5.ComputerHash(System.Text.Encoding.UTF8.GetBytes(myQueueItem.Email + "secret"));
        outputBlob.WriteLine($"Secret code: {BitConverter.ToString(hash).Replace("-","")}");
    }

    // w przypadku  out string outputBlob, mozemy serializowac json
    // outputBlob = JsonConvert.SerializeObject(jakisTamItemKtoryJestZdefiniowanyJakoClass);
}


// SENDING EMAIL
// we select previous Blob and click GO for action, choose BLOBTRIGGERED function, name "EmailLicence" , change path to licenses/{filename}.lic
#r "SendGrid"   // refernece dll
using System.Text.RegularExpressions;
using SendGrid.Helpers.Mail;
public static void Run(string myBlob, string filename, TraceWriter log, out Mail message)
{
    var email = Regex.Match(myBlob, @"^Email\:\ (.+)$", RegexOptions.Multiline).Groups[1].Value;
    log.Info($"Got order from {email}\nLicense file name: {filename}");

    message = new Mail();
    var personalization = new Personalization();
    personalization.AddTo(new Email(email));
    message.AddPersonalization(personalization);

    Attachment attachment = new Attachment();
    var plainTextBytes = System.Text.Encoding.UTF8.GetBytes(myBlob);
    attachment.Content = System.Convert.ToBase64String(plainTextBytes);
    attachment.Type = "text/plain";
    attachment.Filename = "license.lic"
    attachment.Disposition = "attachment";
    attachment.ContentId = "License File";
    message.AddAttachment(attachment);

     var messageContent = new Content("text/html", "Your license file is attached");
    message.AddContent(messageContent);
    message.Subject = "Thanks for your order";
    message.From = new Email("aaa@me.com");
    // https://app.pluralsight.com/player?course=azure-functions-fundamentals&author=mark-heath&name=azure-functions-fundamentals-m4&clip=3&mode=live
}
// you need sendGrid API key, type it in function app properties

// we can also replace filename which is guid now, with license number (e.g 106.lic), and also query azure table storage
// for email associated with this license number, as it will be stored in a row under column, so we use for Email Licence Function INPUT Azure Table Storage
// param name:  ordersRow          table name: orders         partition key: Orders       Row Key:  {filename}
// later in function we may add extra field
public static void Run(string myBlob, string filename, Order ordersRow,  TraceWriter log, out Mail message)   // Order ordersRow ADDED
{ var email = ordersRow.Email ; }   // replace Regex with this




///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
public class CreateGreetingsRequest     // as separate file csx in function called "SharedCode" (created as ManualTrigger)
{
    public string Number;
    public string FirstName;
    public override string ToString() => $"{FirstName} {Number}";
}

// to make other files watch those shared folder for changes, edit  /site/wwwroot/host.json  (in kudu or app service editor)
{
    "watchDirectories": [ "SharedCode" ]
}
// later to load that file into any other file, code, we need:
#load "..\sharedcode\CreateGreetingsRequest.csx"

// QueueTrigger  https://app.pluralsight.com/player?course=azure-function-triggers-quick-start&author=jason-roberts&name=azure-function-triggers-quick-start-m1&clip=5&mode=live

/////////////////  ------------------- ////////////////////
https://msdnshared.blob.core.windows.net/media/2016/11/image764.png

https://github.com/markheath/azure-functions-links