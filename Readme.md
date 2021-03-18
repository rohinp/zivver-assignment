# Zivver Assignment

>.	[OPTIONAL] Have a look at  https://github.com/emilybache/GildedRose-Refactoring-Kata. How would you address a programming problem like this? Could you give an adequate solution in a language of choice? 

* Solution in the package com.gildedrose.
* Please check the [Gildedrose.md](src/main/scala/com/gildedrose/Gildedrose.md) file for details on the solutions and approach

> Huge file read/write 

* Solution in the com.bulkread package.
* Please check the respective [BulkRead.md](src/main/scala/com/bulkread/BulkRead.md) file for details on design decision and algorithm

> Binary search tree related question please check in [BSTree.scala](src/main/scala/com/binarytree/BSTree.scala)

> MongoDB pros and cons

<p> Worked long time back on mongo, putting here the things which I remember.
<p>Pros:

1. Schema less 
2. In memory map reduce to process data plus the query mechanism is also simple.
3. Full text search support 
4. Really fast, where read oriented schema is created compared to RDBMS
5. Distributed document store is also a plus, replicated data, etc.

<p>Cons:

1. No ACID properties or not suited for such senarios.
2. Master slave + config servers can also become a single point of fialure
3. Eventual conistency also an issue, depending on situations.


> You're about to start your new job at a cool Amsterdam startup. What do you favor the most? Pick one.

Ans. Choose your own hardware, be forced to work with a company supplied operating system image.

> When a user logs in on our API, a JWT token is issued and our webapp uses this token for every request for authentication. Here's an example of such a token

Ans. Unless and untill there is no sensitive information in the payload. It is fine to use. But for the given token there is a flag admin which is false and might be a cause for security risk. Apart from that I dont see any situations to use this in a session based auth token. Also we can encript the token to make it more secure.


