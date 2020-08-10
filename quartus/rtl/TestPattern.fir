;buildInfoPackage: chisel3, version: 3.3.2, scalaVersion: 2.12.11, sbtVersion: 1.3.10
circuit TestPattern : 
  module VideoTiming : 
    input clock : Clock
    input reset : Reset
    output io : {flip cen : UInt<1>, video : {enable : UInt<1>, pos : {x : UInt<9>, y : UInt<9>}, hSync : UInt<1>, vSync : UInt<1>, hBlank : UInt<1>, vBlank : UInt<1>}}
    
    reg x : UInt<9>, clock with : (reset => (reset, UInt<9>("h00"))) @[Counter.scala 29:33]
    wire xWrap : UInt<1>
    xWrap <= UInt<1>("h00")
    when io.cen : @[Counter.scala 67:17]
      node _T = eq(x, UInt<9>("h017d")) @[Counter.scala 38:24]
      node _T_1 = add(x, UInt<1>("h01")) @[Counter.scala 39:22]
      node _T_2 = tail(_T_1, 1) @[Counter.scala 39:22]
      x <= _T_2 @[Counter.scala 39:13]
      when _T : @[Counter.scala 41:21]
        x <= UInt<1>("h00") @[Counter.scala 41:29]
        skip @[Counter.scala 41:21]
      xWrap <= _T @[Counter.scala 67:24]
      skip @[Counter.scala 67:17]
    node _T_3 = and(io.cen, xWrap) @[VideoTiming.scala 108:35]
    reg y : UInt<9>, clock with : (reset => (reset, UInt<9>("h00"))) @[Counter.scala 29:33]
    wire yWrap : UInt<1>
    yWrap <= UInt<1>("h00")
    when _T_3 : @[Counter.scala 67:17]
      node _T_4 = eq(y, UInt<9>("h0110")) @[Counter.scala 38:24]
      node _T_5 = add(y, UInt<1>("h01")) @[Counter.scala 39:22]
      node _T_6 = tail(_T_5, 1) @[Counter.scala 39:22]
      y <= _T_6 @[Counter.scala 39:13]
      when _T_4 : @[Counter.scala 41:21]
        y <= UInt<1>("h00") @[Counter.scala 41:29]
        skip @[Counter.scala 41:21]
      yWrap <= _T_4 @[Counter.scala 67:24]
      skip @[Counter.scala 67:17]
    node _T_7 = sub(x, UInt<6>("h022")) @[VideoTiming.scala 111:18]
    node _T_8 = tail(_T_7, 1) @[VideoTiming.scala 111:18]
    node _T_9 = sub(y, UInt<5>("h013")) @[VideoTiming.scala 111:41]
    node _T_10 = tail(_T_9, 1) @[VideoTiming.scala 111:41]
    wire pos : {x : UInt<9>, y : UInt<9>} @[Pos.scala 64:19]
    pos.x <= _T_8 @[Pos.scala 65:11]
    pos.y <= _T_10 @[Pos.scala 66:11]
    node _T_11 = geq(x, UInt<9>("h0167")) @[VideoTiming.scala 114:17]
    node _T_12 = lt(x, UInt<9>("h017e")) @[VideoTiming.scala 114:45]
    node hSync = and(_T_11, _T_12) @[VideoTiming.scala 114:40]
    node _T_13 = geq(y, UInt<9>("h010f")) @[VideoTiming.scala 115:17]
    node _T_14 = lt(y, UInt<9>("h0111")) @[VideoTiming.scala 115:45]
    node vSync = and(_T_13, _T_14) @[VideoTiming.scala 115:40]
    node _T_15 = geq(x, UInt<6>("h022")) @[VideoTiming.scala 118:20]
    node _T_16 = lt(x, UInt<9>("h0162")) @[VideoTiming.scala 118:51]
    node hDisplay = and(_T_15, _T_16) @[VideoTiming.scala 118:46]
    node _T_17 = geq(y, UInt<5>("h013")) @[VideoTiming.scala 119:20]
    node _T_18 = lt(y, UInt<9>("h0103")) @[VideoTiming.scala 119:51]
    node vDisplay = and(_T_17, _T_18) @[VideoTiming.scala 119:46]
    io.video.pos.y <= pos.y @[VideoTiming.scala 122:16]
    io.video.pos.x <= pos.x @[VideoTiming.scala 122:16]
    io.video.hSync <= hSync @[VideoTiming.scala 123:18]
    io.video.vSync <= vSync @[VideoTiming.scala 124:18]
    node _T_19 = eq(hDisplay, UInt<1>("h00")) @[VideoTiming.scala 125:22]
    io.video.hBlank <= _T_19 @[VideoTiming.scala 125:19]
    node _T_20 = eq(vDisplay, UInt<1>("h00")) @[VideoTiming.scala 126:22]
    io.video.vBlank <= _T_20 @[VideoTiming.scala 126:19]
    node _T_21 = and(hDisplay, vDisplay) @[VideoTiming.scala 127:31]
    io.video.enable <= _T_21 @[VideoTiming.scala 127:19]
    
  module TestPattern : 
    input clock : Clock
    input reset : UInt<1>
    output io : {video : {enable : UInt<1>, pos : {x : UInt<9>, y : UInt<9>}, hSync : UInt<1>, vSync : UInt<1>, hBlank : UInt<1>, vBlank : UInt<1>}, rgb : {r : UInt<4>, g : UInt<4>, b : UInt<4>}}
    
    inst videoTiming of VideoTiming @[TestPattern.scala 62:27]
    videoTiming.clock <= clock
    videoTiming.reset <= reset
    videoTiming.io.cen <= UInt<1>("h01") @[TestPattern.scala 63:22]
    node _T = bits(videoTiming.io.video.pos.x, 2, 0) @[TestPattern.scala 67:20]
    node _T_1 = eq(_T, UInt<1>("h00")) @[TestPattern.scala 67:27]
    node _T_2 = bits(videoTiming.io.video.pos.y, 2, 0) @[TestPattern.scala 67:48]
    node _T_3 = eq(_T_2, UInt<1>("h00")) @[TestPattern.scala 67:55]
    node _T_4 = or(_T_1, _T_3) @[TestPattern.scala 67:35]
    node _T_5 = mux(_T_4, UInt<6>("h03f"), UInt<1>("h00")) @[TestPattern.scala 67:8]
    node _T_6 = bits(videoTiming.io.video.pos.x, 4, 4) @[TestPattern.scala 68:20]
    node _T_7 = mux(_T_6, UInt<6>("h03f"), UInt<1>("h00")) @[TestPattern.scala 68:8]
    node _T_8 = bits(videoTiming.io.video.pos.y, 4, 4) @[TestPattern.scala 69:20]
    node _T_9 = mux(_T_8, UInt<6>("h03f"), UInt<1>("h00")) @[TestPattern.scala 69:8]
    wire rgb : {r : UInt<4>, g : UInt<4>, b : UInt<4>} @[RGB.scala 107:19]
    rgb.r <= _T_5 @[RGB.scala 108:11]
    rgb.g <= _T_7 @[RGB.scala 109:11]
    rgb.b <= _T_9 @[RGB.scala 110:11]
    io.video.vBlank <= videoTiming.io.video.vBlank @[TestPattern.scala 73:12]
    io.video.hBlank <= videoTiming.io.video.hBlank @[TestPattern.scala 73:12]
    io.video.vSync <= videoTiming.io.video.vSync @[TestPattern.scala 73:12]
    io.video.hSync <= videoTiming.io.video.hSync @[TestPattern.scala 73:12]
    io.video.pos.y <= videoTiming.io.video.pos.y @[TestPattern.scala 73:12]
    io.video.pos.x <= videoTiming.io.video.pos.x @[TestPattern.scala 73:12]
    io.video.enable <= videoTiming.io.video.enable @[TestPattern.scala 73:12]
    wire _T_10 : {r : UInt<4>, g : UInt<4>, b : UInt<4>} @[RGB.scala 92:19]
    _T_10.r <= UInt<1>("h00") @[RGB.scala 93:11]
    _T_10.g <= UInt<1>("h00") @[RGB.scala 94:11]
    _T_10.b <= UInt<1>("h00") @[RGB.scala 95:11]
    node _T_11 = mux(videoTiming.io.video.enable, rgb, _T_10) @[TestPattern.scala 74:16]
    io.rgb.b <= _T_11.b @[TestPattern.scala 74:10]
    io.rgb.g <= _T_11.g @[TestPattern.scala 74:10]
    io.rgb.r <= _T_11.r @[TestPattern.scala 74:10]
    
