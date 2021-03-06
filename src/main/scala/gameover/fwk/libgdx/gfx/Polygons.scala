package gameover.fwk.libgdx.gfx

import com.badlogic.gdx.math.{Polygon, Vector2}
import gameover.fwk.libgdx.utils.LibGDXHelper
import gameover.fwk.pool.Vector2Pool

object Polygons extends LibGDXHelper {

  def createArcAsPolygon(x: Float, y: Float, radius: Float, start: Float, angle: Float, segments: Int): Polygon = {
    if (segments < 1) throw new IllegalArgumentException("arc need at least 1 segment")
    val theta: Float = (2 * 3.1415926f * (angle / 360.0f)) / segments
    val cos: Float = com.badlogic.gdx.math.MathUtils.cos(theta)
    val sin: Float = com.badlogic.gdx.math.MathUtils.sin(theta)
    var cx: Float = radius * com.badlogic.gdx.math.MathUtils.cos(start * com.badlogic.gdx.math.MathUtils.degreesToRadians)
    var cy: Float = radius * com.badlogic.gdx.math.MathUtils.sin(start * com.badlogic.gdx.math.MathUtils.degreesToRadians)
    val vertices: Array[Float] = new Array[Float](segments * 2 + 2)
    vertices(vertices.length - 2) = x
    vertices(vertices.length - 1) = y
    for (i <- 0 to segments - 1) {
      val temp: Float = cx
      cx = cos * cx - sin * cy
      cy = sin * temp + cos * cy
      vertices(i * 2) = x + cx
      vertices(i * 2 + 1) = y + cy
    }
    new Polygon(vertices)
  }

  def isPointInPolygon(polygon: GdxArray[Vector2], pointX: Float, pointY: Float): Boolean = {
    val lastVertice: Vector2 = polygon.get(polygon.size - 1)
    var lastVerticeX = lastVertice.x
    var lastVerticeY = lastVertice.y
    var oddNodes: Boolean = false
    for (i <- polygon.indices) {
      val v: Vector2 = polygon.get(i)
      val x = v.x
      val y = v.y
      if (y < pointY && lastVerticeY >= pointY || lastVerticeY < pointY && y >= pointY) {
        if (x + (pointY - y) / (lastVerticeY - y) * (lastVerticeX - x) < pointX) {
          oddNodes = !oddNodes
        }
      }
      lastVerticeX = x
      lastVerticeY = y
    }
    oddNodes
  }

  def createArcAsListOfVertices(x: Float, y: Float, radius: Float, start: Float, angle: Float, segments: Int, fromPool: Boolean): GdxArray[Vector2] = {
    if (segments < 1) throw new IllegalArgumentException("arc need at least 1 segment")
    val theta: Float = (2 * 3.1415926f * (angle / 360.0f)) / segments
    val cos: Float = com.badlogic.gdx.math.MathUtils.cos(theta)
    val sin: Float = com.badlogic.gdx.math.MathUtils.sin(theta)
    var cx: Float = radius * com.badlogic.gdx.math.MathUtils.cos(start * com.badlogic.gdx.math.MathUtils.degreesToRadians)
    var cy: Float = radius * com.badlogic.gdx.math.MathUtils.sin(start * com.badlogic.gdx.math.MathUtils.degreesToRadians)
    var ret: GdxArray[Vector2] = null
    if (fromPool) {
      ret = Vector2Pool.obtainAsGdxArray(segments + 1)
    }
    else {
      def f = () => new Vector2
      ret = new GdxArray[Vector2]().fill(segments + 1)(f)
    }
    for (i <- 0 to segments) {
      val temp: Float = cx
      cx = cos * cx - sin * cy
      cy = sin * temp + cos * cy
      ret.get(i).set(x + cx, y + cy)
    }
    ret.get(segments).set(x, y)
    ret
  }
}
