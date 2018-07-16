# Starts an interactive session with a remote computer.
# https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.core/enter-pssession?view=powershell-6
# https://blogs.technet.microsoft.com/uktechnet/2016/02/11/configuring-winrm-over-https-to-enable-powershell-remoting/

# PREZENTACJA O DATA FACTORY
https://github.com/kromerm/adfbuild2018
https://gallery.azure.ai//Tutorial/Data-Warehousing-and-Data-Science-with-SQL-Data-Warehouse-and-Spark-3

# DEBUG
https://youtu.be/3enkNvprfm4
https://docs.microsoft.com/en-us/azure/data-lake-analytics/data-lake-analytics-debug-u-sql-jobs



Enter-PSSession -ComputerName 13.90.222.222 -Credential (Get-Credential) -UseSSL -SessionOption (New-PSSessionOption) -SkipCACheck -SkipCNCheck
# same as
invoke-command -ComputerName xxx .... -ScriptBlock {Get-WindowsFeature} 



# check if something is installed on Windows server
Get-WindowsFeature web-server | install-windowsFeature

-VMName configureme  , zeby wykonac instalacje na obecnej maszynie