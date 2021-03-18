package com.binarytree

//version 1 in scala 2
object scala2:
  /**
   * Ordering enforces that you have an elment in the tree which can be ordered
   *  1. It's only on the Node, as it contains data
   *  2. Ordering helps to insert data in tree to make it a BST
   *  3. We can't apply context bound on trait, we could have used abstract class but then we have issues with nothing
   */
  sealed trait Tree[+E]
  case object Empty extends Tree[Nothing]
  case class Node[+E:Ordering](left:Tree[E], data:E, right:Tree[E]) extends Tree[E]

object scala3:
  /**
   * First create a simple tree ADT using enum.
   * Can say syntactic sugar for trait and class, objects.
   */
  enum Tree[+E]:
    case Empty
    case Node(left:Tree[E], data:E, right:Tree[E])

  /**
   * Second create a contextual function to make T ordered
   * This seems a very elegant solution
  */
  type BSTree[T] = Ordering[T] ?=> Tree[T]

  /**
   * For other programing languages?
   * ADT is a very general way to represent domain/state
   * Irrispective on of language we can have ADT's.
   * In this case a recursive ADT.
   * I suppose we must be able to construct one in any programming languge unless untill it gives provision for 
   * Create data with relations
   */

   /**
    * Before I start with pros and cons on tree trevarsal. Weather to use recursion or not actually evolves from the notion of what
    * data structure modeling we are dealing with. Here more specifically recursive ADT's. If it is recursive ADT, automatically 
    * recursive code fits with the solution. If the modeling is not recursive obviously it drives the imparative/iterative solution.
    * Credits: https://htdp.org/ how to design programs :-)
    * 
    * Tree traversal:
    *   Recursive:
    *     1. approach is really simple, declarative and readable, 
    *     2. but obviusly there is a catch you might not want to blow up your stack for really huge recursion, or wrongly implemented solution.  
    *     3. That is where `Trempolining` comes into picture. It's like saying move the recursion to heap rather than stack. 
    *     4. Now this is specific to JVM. Also with new project loom merging in to jdk, which will support tail call elemination, recusions will be well supported by Java platform.
    * 
    * But with current versions of Java, we can always go for tempolining using scala3 TailCall 
    * functions or in scala2 implementing our own Trempolining with Free Monads.
    *
    *   Iterative:
    *     1. approach is also good but it has too many moving parts because of it's imparative nature.
    *     2. Again it depends on the modeling, if the model fits with iterative nature then obviously it fits.
    *     3. Plus point it can really be performant solution, again depends on modeling as well 
    *     4. Obvioudly depending on the situation and tech stack used we can decide which one to go for.
    * 
    * There is no direct yes/no answer though. As said already it depends on what underlying platform we are on
    * and what tooling is avilable (for example trempolining, tail call elemination, non recursive data model etc.)
    */