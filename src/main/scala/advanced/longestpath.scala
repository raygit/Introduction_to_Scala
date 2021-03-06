
case class Node(value: Int, coord: (Int, Int)) 

object FindLongestPath {
    val sentinel = (-42,-42)
    val LIMIT = 10
    val ROWS = 4
    val COLS = 4
    import util.Random._

    def within(p1: (Int,Int)) = (p2:(Int,Int)) => {
        val (x1, x2) = (fst(p1), fst(p2))
        val (y1, y2) = (snd(p1), snd(p2))
        (x1 >= 0) && (x2 >= 0) && (y1 >= 0) && (y2 >= 0) && (y1 < COLS) && (x1 < ROWS)
    }

	val UpperLeft   = (0,0)
    val UpperRight  = (0,COLS)
    val BottomLeft  = (ROWS, 0)
    val BottomRight = (ROWS, COLS)

    private def fst(pair: (Int,Int)) = pair._1
    private def snd(pair: (Int,Int)) = pair._2
    private def pretty_print(matrix: Seq[Seq[Int]]) =
        matrix.map(l => l.mkString("|")).map(s => s + "\n").mkString

    def generateMatrix(rows: Int, cols: Int) = Seq.fill(rows, cols){
        { // anon-fn to remove zero-values.
              def tryAgain(x:Int) : Int = {
              val t = nextInt(x)
              t match {
                case 0 => tryAgain(x)
                case x => x
              }}
              tryAgain(LIMIT)
        }
    }

    def smallestElevation = (m: Seq[Seq[Int]]) => (m.map(l => l.min)).min

    private def getStartingPoint(rows: Int, cols: Int) = {
        import util.Random._
        (nextInt(rows), nextInt(cols))
    }
    def left = (rows:Int) => (cols:Int) =>
        (cols - 1) < 0 match {
            case false => (rows, cols - 1)
            case _     => sentinel
        }

    def right = (rows: Int) => (cols: Int) =>  
        (cols + 1) >= COLS match {
            case false => (rows, cols + 1)
            case _     => sentinel
        }

    def up = (rows:Int) => (cols: Int) =>
        (rows - 1) < 0 match {
            case false => (rows - 1, cols)
            case true  => sentinel
        }

    def down = (rows:Int) => (cols: Int) =>
        (rows + 1) >= ROWS match {
            case false => (rows + 1, cols)
            case true  => sentinel
        }

    def upperleft = (rows:Int) => (cols:Int) =>
        within((rows - 1, cols -1))(UpperLeft) match {
            case true   => (rows - 1, cols - 1)
            case false  => sentinel
        }

    def upperright = (rows: Int) => (cols:Int) =>
        within((rows - 1, cols + 1))(UpperRight) match {
            case true  => (rows - 1, cols + 1)
            case false => sentinel
        }

    def bottomleft = (rows: Int) => (cols:Int) =>
        within((rows +1 , cols - 1))(BottomLeft) match {
            case true  => (rows + 1, cols - 1)
            case false => sentinel
        }

    def bottomright = (rows: Int) => (cols: Int) =>
        within((rows + 1, cols + 1))(BottomRight) match {
            case true  => (rows + 1, cols + 1)
            case false => sentinel
        }


    private def findNeighbours(matrix: Seq[Seq[Int]])(coords: (Int, Int)) : Seq[Node] = {
        val x = fst(coords)
        val y = snd(coords)

        val currentElevation = matrix(x)(y)
        (currentElevation == smallestElevation(matrix)) match {
            case true  => Nil // stop the search coz you've found it!
            case false =>     // continue the search ... 
                val a = 
		        Seq(left, right, up, down, upperleft, upperright, bottomleft, bottomright).
		        map(f => f(x)(y)).
		        filter( pair => fst(pair) != fst(sentinel) &&  // not the sentinel
		                        snd(pair) != snd(sentinel) && 
		                        matrix(fst(pair))(snd(pair)) < currentElevation). // found a new place to descend !
		        map(pair => Node(matrix(fst(pair))(snd(pair)), pair)) // pass the information along ..
                println("found neighbours of " + (x,y) + " to be " + a)
                a
        }
    }

    def findLongestPath(matrix: Seq[Seq[Int]])(rows: Int, cols: Int) = {
        val start = getStartingPoint(rows, cols) 
        println("All paths from " + start + " is " + visit(matrix)(start))
    }

    def visit(matrix: Seq[Seq[Int]])(position: (Int,Int)) : Seq[Node] = {
 
        @scala.annotation.tailrec
        def trace(acc: Seq[Node], remain: Seq[Node]) : Seq[Node] = 
        remain match {
            case Nil    => println("A path is -> " + acc); acc
            case h :: t => trace(acc :+ h, visit(matrix)(h.coord))
        }

        val x = Node(matrix(fst(position))(snd(position)), position)

        findNeighbours(matrix)(position) match {
            case Nil => Nil
            case ns  => trace(Seq(x), ns)
        }
    }

    def main(args : Array[String]) { 
        val matrix = generateMatrix(ROWS, COLS)
        println(ROWS + "x" + COLS + " Matrix is\n " + pretty_print(matrix))
        findLongestPath(matrix)(ROWS, COLS)
    }
 
}

