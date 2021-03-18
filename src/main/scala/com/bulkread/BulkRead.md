# Bulk Read

<p>There are different ways this can be implemented. Though will follow a simple approach of what hadoop does. The concent of map ( -> combine )and reduce. Obviously there is more to it. 
<p>One of the important bit here is selecting a technique to read file, specifically a huge file.

1. In java there are two methods one is the old java.io package and other is java.nio.
2. NIO gives channels through which chunks can be easily read and this used here.
3. Though there are drawbacks as well, specially you need to understand your data in bytes ;-)

### Algorithm details:
<p>Will put the steps along with which file/function contains the code. Again working with combinators always makes life easy. Here combinators to just sequence the operations.
There are some scala3 specific feaures used, as and when we encounter in code will put some comments for explanation.
<p>Here is the algo:

1. Read data from file in chunks of given bytes MapRedice.scala `next` combinator, split stage
2. Convert those chunk in to Records using `toRecords` combinator, map stage.
3. Once the records are processed can be writento file using `writeRecords` combinator
4. But before writing it need some processing
        a. Group the records by id
        b. Sort the data
        b. Creating MetaData which files contain what product Id's
        c. All this take place in `readChunkGroupSortAndWrite` combinator
5. Now repeating the steps 1 to 4 for all the chunks read using combinator `repeat`
6. One the file is divided into chunks with seasoned data along with metadata inhand, shuffeling and reducing can be done.
7. To keep it simple shuffle and reduce are done in one step, reading files in order of product id from all the files and writing to the output or result file.
8. `Using.Manager` works like wonder in this case as I wanted to read from multiple files and write into one file, w.r.t resource management.

### Improvements:
There is lot to improve:
1. The project is missing testing, those because of combinators we can write tests easily and test small parts of the application.
2. Combinators need to be refined to handle exception.
3. Also more smaller combinators, would have made code more testable and extendable.
4. Concurrency, is something can be introduced at different parts of program to improve performance as current implementation is compeltely sequential. Though it'll also add to the complexity of th ecode.
5. Can make it a cmd app, as of now it's a manual change in conf and run app through sbt.

### How to:
<p> There is a `CreateDataFile.scala` utility which can be used to create data files to test the algorithm.

> Note: The algo is tested with as small as 100MB file to as large as 10GB file. And it seems working :-) without frying your CPU and RAM. But yeah depending on your mahcine configuration you might want to change the `Configuration.scala` and import it in the Main.scala

Steps to run:

> $> sbt
> $> runMain com.bulkread.run

## Some stats while runnng on random data generated form the utility given in the project
for 10GB data file, and creating 48MB chunk it took 38minutes and for the heap and CPU usage please check below image
![Jconsole Snapshot after execution](10GBFileProcessing.png)

for 90MB file it took 20seconds.

## Machine on which the program was run
RAM : 32GB
CPU : 8Core
OS : MAC OS