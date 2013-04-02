
sealed trait Tree[+A] {
  def size[U >: A](tree: Tree[U], acc: Int) : Int = 
    tree match {
        case Leaf(a) => acc + 1
        case Branch(left, right) => size(left, acc) + size(right, acc)
  }
}

case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {
  def size[A](tree: Tree[A], acc: Int) : Int = 
    tree match {
        case Leaf(a) => acc + 1
        case Branch(left, right) => size(left, acc) + size(right, acc)
  }

  def max[A](tree: Tree[A])(implicit ord: A => Ordered[A]) : A = 
    tree match {
        case Leaf(a) => a
        case Branch(left, right) => {
            val lhs = max(left)
            val rhs = max(right)
            if (ord(lhs) > rhs) lhs else rhs
        }
    }

  def map[A,B](tree: Tree[A])(f: A => B) : Tree[B] = 
    tree match {
        case Leaf(a) => Leaf(f(a))
        case Branch(left, right) => {
            val lhs = map(left)(f)
            val rhs = map(right)(f)
            Branch(lhs, rhs)
        }
    }

}
