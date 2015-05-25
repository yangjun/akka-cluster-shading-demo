/**
 * 测试类
 * Created by yangjungis@126.com on 2015/5/24.
 */
class C1 {
  def add(x: Int, y: Int): Int = {
    x + y
  }
}

object C1 {
  def main(args: Array[String]): Unit = {
    val c1 = new C1()
    val n = c1.add(1,2)
    println(n)
  }
}