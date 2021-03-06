import annotation._
import scala.language._

object ListObj {
// Variadic functions in Scala
// -------------------------------
// The function List.apply in the listing above is a variadic function,
// meaning it accepts zero or more arguments of type A. For data types, it
// is a common idiom to have a variadic apply method in the companion object
// to conveniently construct instances of the data type. By calling this 
// function 'apply' and placing it in the companion object, we can
// invoke it with syntax like List(1,2,3) or List("hi", "bye"), with as 
// many values as we want separated by commas (we sometimes call this the 
// literal syntax)
// Variadic functions are just providing a little syntax sugar for creating
// and passing a Seq of elements explicitly. Inside apply, as will be bound to a Seq[A]
// which has the functions head and tail.
// We can convert a Seq[A} back into something that can be passed to a variadic
// function using the syntax x:_* 

sealed trait List[+A]
case object Nil extends List[Nothing]
case class ::[+A](head: A, tail: List[A]) extends List[A]
object List {
    def sum(ints: List[Int]) : Int = ints match {
        case Nil => 0
        case ::(h, t) => h + sum(t)
    }
    def product(ds: List[Double]) : Double = ds match {
        case Nil => 1.0
        case ::(h, t) => h * product(t)
    }
    // Bombs out due to StackOverflow error since its not
    // tail-recursive
    def apply[A](as: A*) : List[A] = 
        if (as.isEmpty) Nil else ::(as.head, apply(as.tail:_*))
    def tail[A](l: List[A]) : List[A] = l match {
        case Nil => Nil
        case ::(h, t) => t
    }
    def tailViaDrop[A](l: List[A]): List[A] = drop(l,1)

    def drop[A](l: List[A], n: Int) : List[A] = l match {
        case Nil => Nil
        case ::(h, t) => if (n == 0) l else drop(t, n - 1)
    }
    def dropWhile[A](l: List[A])(f: A => Boolean) : List[A] = l match {
        case Nil => Nil
        case ::(h, t) if (f(h)) => dropWhile( drop(l, 1) )(f)
        case ::(h, _) if !f(h) => l
    }

    def setHead[A](l: List[A], value: A) : List[A] = l match {
        case Nil => ::(value, Nil)
        case ::(x, xs) => ::(value, xs)
    }

    def append[A](a1: List[A], a2: List[A]) : List[A] = a1 match {
        case Nil => a2
        case ::(h, t) => ::(h, append(t, a2))
    }
    // In the current implementation, we have to walk the entire list
    // inorder to determine and removed the last element, if any.
    // Other possible implementations would include the notions of 'head','tail'
    // 'tailElement' or even allow each element to be indexable like Java's ArrayList.
    def init[A](l : List[A]) : List[A] = l match {
        case Nil => Nil
        case ::(h, Nil) => Nil
        case ::(h, t) => ::(h, init(t))
    }

    // With a large list, there will be a stack overflow problem 
    def foldRight[A,B](l: List[A], z: B)(f: (A,B) => B) : B = 
        l match {
            case Nil => z
            case ::(h, t) => f(h, foldRight(t,z)(f))
        }

    def length[A](l: List[A]) : Int = foldRight(l, 0)((a,b) => b + 1)

    @tailrec
    def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B) : B = 
        l match {
            case Nil => z
            case ::(h, t) => foldLeft(t, f(z,h))(f)
        }

    def reverse[A](l : List[A]) : List[A] = foldLeft(l, Nil:List[A])((l,e) => ::(e,l))

    // The interesting formulation of this equation is the fact that the expression
    // '(b:B) => b' is like an identity function which returns exactly the same thing it was passed
    // 
    def foldLeftViaFoldRight[A,B](l: List[A], z: B)(f: (B,A) => B) : B = foldRight(l, (b:B) => b)((a,g) => b => g(f(b,a)))(z)

    // The concept of folding is quite natural when you examine how the function works. Its akin to 
    // folding clothes from the left or right but you may wish to do something prior to the fold.
    def appendViaFoldLeft[A](a1: List[A], a2: List[A]) : List[A] = foldLeft(reverse(a1), a2)((a2,e) => ::(e,a2))

    def appendViaFoldRight[A](a1: List[A], a2: List[A]) : List[A] = foldRight(a1, a2)((e,a2) => ::(e, a2))

    // its runtime is definitely linear i.e. O(n) 
    def concat[A](ll: List[List[A]]) : List[A] = foldRight(ll, Nil:List[A])(append) 

    def map[A,B](l : List[A])(f: A => B) : List[B] = foldRight(l, Nil:List[B])((e,l) => ::(f(e), l))

    def filter[A](l : List[A])(f: A => Boolean) : List[A] = foldRight(l , Nil:List[A])((e,l) => if (f(e)) ::(e,l) else l)

    def flatMap[A,B](l : List[A])(f: A => List[B]) : List[B] = concat(map(l)(f)) 

    def filterViaFlatMap[A](l : List[A])(f: A => Boolean) : List[A] = flatMap(l)( e => if (f(e)) List(e) else Nil)

    def zip(l: List[Int], r: List[Int]) : List[Int] = (l,r) match {
        case (::(h, t), ::(h2, t2)) => ::(h+h2, zip(t, t2))
        case (Nil, _) => Nil
        case (_, Nil) => Nil
    }

    def zip[A](l: List[A], r: List[Int])(f: (Int,Int) => Int) : List[Int] = (l,r) match {
        case (Nil, _) => Nil
        case (_, Nil) => Nil
        case (::(h:Int, t), ::(h2:Int, t2)) => ::(f(h,h2), zip(t, t2)(f))
        case (::(_,_), ::(_,_)) => Nil
    }
}
}

// Writing purely functional data structures that support different operations efficiently is all
// about finding clever ways to exploit data sharing, which often means working with more tree-like
// data structures.

// ADTs and encapsulation
// One might object that algebraic data types violate encapsulation by making
// public the internal representation of a type. In FP, we approach
// concerns about encapsulation a bit differently - we don't typically have
// delicate mutable state which could lead to bugs or violation of invariants
// if exposed publicly. Exposing the data constructors of a type is often
// fine, and the decision to do so is approached much liek any other decision
// about what the public API of a data type should be.

object OptionalTree {
	sealed trait Tree[+A]
	case class Leaf[A](value: A) extends Tree[A] 
	case class Branch[A](left: Option[Tree[A]], right: Option[Tree[A]]) extends Tree[A]
	object Tree {
	    // Overkill? probably...but its an exercise of using type-parameters to capture types
        // but there's a problem, the implicit-lookup does not appear to be able to look up
        // `lift` with the context-bound, but had to invoke it explicitly
        // Q: Is it because of my knowledge-gap w.r.t PartialFunction[Any,A] in the pattern-matching ?
	    implicit def lift[R <: Option[Tree[_]],A](t: R ) : Tree[A] = 
	        t match {
	            case None => None
	            case Some(v:Tree[A]) => v
	        }
	    def size(t: Option[Tree[Int]])(implicit w: Option[Tree[Int]] => Tree[Int]) : Int = w(t) match {
	        case Leaf(a) => 1
	        case Branch(lhs, rhs) => 1 + size(lhs) + size(rhs)
	    }
	
	    def maximum(t: Option[Tree[Int]])(implicit w: Option[Tree[Int]] => Tree[Int]): Int = w(t) match {
	        case Leaf(n) => n
	        case Branch(lhs, rhs) => maximum(lhs) max maximum(rhs)
	    }
	    def depth[A](t: Option[Tree[A]])(implicit w: Option[Tree[A]] => Tree[A]) : Int = w(t) match {
	        case Branch(lhs, rhs) => 1 + (depth(lhs) max depth(rhs))
	        case Leaf(_) => 1
	    }
	    def map[A,B](t: Option[Tree[A]])(f: A => B)(implicit w: Option[Tree[A]] => Tree[A]): Option[Tree[B]] = w(t) match {
	        case Leaf(a) => Some(Leaf(f(a)))
	        case Branch(lhs, rhs) => Some(Branch(map(lhs)(f) , map(rhs)(f)))
	    }

}
}
