# Payslip Utility

Utility to download all payslips and yearly statements from https://werknemerloket.nl. The documents will be downloaded into
one directory per employment. You should end up with a number of *Loonstrook\*.pdf* and *Jaaropgaaf\*.pdf* files. 

## Usage:

Build with `mvn clean package`

Run with `java -jar payslip-util.jar`

It should ask you for your username (email), password and the directory to output the PDFs to. Alternatively you can supply 
these when running the application:

    java -jar payslip-util.jar <username> <password> <directory>
