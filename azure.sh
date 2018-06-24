# Starts an interactive session with a remote computer.
# https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.core/enter-pssession?view=powershell-6
# https://blogs.technet.microsoft.com/uktechnet/2016/02/11/configuring-winrm-over-https-to-enable-powershell-remoting/
Enter-PSSession -ComputerName 13.90.222.222 -Credential (Get-Credential) -UseSSL -SessionOption (New-PSSessionOption) -SkipCACheck -SkipCNCheck

