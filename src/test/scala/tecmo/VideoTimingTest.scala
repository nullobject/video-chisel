/*
 *    __   __     __  __     __         __
 *   /\ "-.\ \   /\ \/\ \   /\ \       /\ \
 *   \ \ \-.  \  \ \ \_\ \  \ \ \____  \ \ \____
 *    \ \_\\"\_\  \ \_____\  \ \_____\  \ \_____\
 *     \/_/ \/_/   \/_____/   \/_____/   \/_____/
 *    ______     ______       __     ______     ______     ______
 *   /\  __ \   /\  == \     /\ \   /\  ___\   /\  ___\   /\__  _\
 *   \ \ \/\ \  \ \  __<    _\_\ \  \ \  __\   \ \ \____  \/_/\ \/
 *    \ \_____\  \ \_____\ /\_____\  \ \_____\  \ \_____\    \ \_\
 *     \/_____/   \/_____/ \/_____/   \/_____/   \/_____/     \/_/
 *
 *  https://joshbassett.info
 *  https://twitter.com/nullobject
 *  https://github.com/nullobject
 *
 *  Copyright (c) 2020 Josh Bassett
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package tecmo

import chisel3._
import chiseltest._
import org.scalatest._

class VideoTimingTest extends FlatSpec with ChiselScalatestTester with Matchers {
  val config = VideoTimingConfig()

  behavior of "x position"

  it should "be incremented when the clock enable is asserted" in {
    test(new VideoTiming(config)) { c =>
      c.io.cen.poke(false.B)
      c.clock.step()
      c.io.video.pos.x.expect(466.U)

      c.io.cen.poke(true.B)
      c.clock.step()
      c.io.video.pos.x.expect(467.U)
    }
  }

  behavior of "horizontal sync"

  it should "be asserted after the horizontal front porch" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.hSync.expect(false.B)
      c.clock.step()
      c.io.video.hSync.expect(true.B)
    }
  }

  it should "be deasserted after the horizontal retrace" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch+config.hRetrace-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.hSync.expect(true.B)
      c.clock.step()
      c.io.video.hSync.expect(false.B)
    }
  }

  behavior of "horizontal blank"

  it should "be asserted at the end of the horizontal display region" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.hBlank.expect(false.B)
      c.clock.step()
      c.io.video.hBlank.expect(true.B)
    }
  }

  it should "be deasserted at the start of the horizontal display region" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.hBlank.expect(true.B)
      c.clock.step()
      c.io.video.hBlank.expect(false.B)
    }
  }

  behavior of "y position"

  it should "be incremented after the horizontal sync" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.pos.y.expect(0.U)
      c.clock.step()
      c.io.video.pos.y.expect(1.U)
    }
  }

  behavior of "vertical sync"

  it should "be asserted after the vertical front porch" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch-1,
      yInit = config.vBackPorch+config.vDisplay+config.vFrontPorch-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.vSync.expect(false.B)
      c.clock.step()
      c.io.video.vSync.expect(true.B)
    }
  }

  it should "be deasserted after the vertical retrace" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch-1,
      yInit = config.vBackPorch+config.vDisplay+config.vFrontPorch+config.vRetrace-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.vSync.expect(true.B)
      c.clock.step()
      c.io.video.vSync.expect(false.B)
    }
  }

  behavior of "vertical blank"

  it should "be asserted at the end of the vertical display region" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch-1,
      yInit = config.vBackPorch+config.vDisplay-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.vBlank.expect(false.B)
      c.clock.step()
      c.io.video.vBlank.expect(true.B)
    }
  }

  it should "be deasserted at the start of the vertical display region" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay+config.hFrontPorch-1,
      yInit = config.vBackPorch-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.vBlank.expect(true.B)
      c.clock.step()
      c.io.video.vBlank.expect(false.B)
    }
  }

  behavior of "enable"

  it should "be asserted at the start of the horizontal and vertical display regions" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch-1,
      yInit = config.vBackPorch
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.enable.expect(false.B)
      c.clock.step()
      c.io.video.enable.expect(true.B)
    }
  }

  it should "be deasserted at the end of the horizontal and vertical display regions" in {
    test(new VideoTiming(
      config = config,
      xInit = config.hBackPorch+config.hDisplay-1,
      yInit = config.vBackPorch+config.vDisplay-1
    )) { c =>
      c.io.cen.poke(true.B)

      c.io.video.enable.expect(true.B)
      c.clock.step()
      c.io.video.enable.expect(false.B)
    }
  }
}
