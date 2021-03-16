# Bulk Read

<p>There are different ways this can be implemented. Though will follow a simple approach of what hadoop does. The concent of map (-> combine )and reduce. Obviously there is more to it. <p>One of the important bit here is selecting a technique to read file, specifically a huge file.

1. In java there are two methods one is the old java.io package and other is java.nio.
2. NIO would have been an ideal solution, where we would have dealt with block of data.
3. But for simplicity I'm sticking to the old io capabilities which rely on stream.


### Algorithm details:

1. Split files into chunks (chunk size 10M records). While spliting, following intermediate steps happen.

        1. group by product id in each chunck
        2. tmp files records pid,c1,c2,c3...
        3. Also before writing sort the chunk data based on id.
        4. Write files in the output/tmp/<fileName>-Split_Map-<Counter>.csv
        5. For every chunk manitain meta data in memory (in a key-value). Meta data containing list of unique id's in a chunck of file.

2. Now we have huge file broken into smaller chunks and sorted data. We also have meta data in memory which will help for the next steps.


        1. This is a shuffeling and merging stage.
        2. Based on the meta data we have, can be decided which records needs to be moved to which files or merged with which files.
        3. Special care needs to be take that the data remains sorted based on prod id.
        4. The result of this stage will be present in the combine folder.
        5. This stage will update the meta data. So that which chunk hav which pid are known.

3. Now that we have sorted data broken into multiple files and the data is sorted. Based on meta data we can start merging the file into one.
        
        1. This stage is called reduce state.
        2. As the name suggest we will just take the chunk from the files and dump as it in sequentially in to a single file.
        3. No special care needed as the data is sorted and based on the meta information the sequence of dumping is simple and straightforward.