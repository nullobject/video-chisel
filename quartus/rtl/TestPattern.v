module VideoTiming(
  input        clock,
  input        reset,
  output       io_video_enable,
  output [8:0] io_video_pos_x,
  output [8:0] io_video_pos_y,
  output       io_video_hSync,
  output       io_video_vSync,
  output       io_video_hBlank,
  output       io_video_vBlank
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [8:0] x; // @[Counter.scala 29:33]
  wire  xWrap = x == 9'h17d; // @[Counter.scala 38:24]
  wire [8:0] _T_2 = x + 9'h1; // @[Counter.scala 39:22]
  reg [8:0] y; // @[Counter.scala 29:33]
  wire  _T_4 = y == 9'h110; // @[Counter.scala 38:24]
  wire [8:0] _T_6 = y + 9'h1; // @[Counter.scala 39:22]
  wire  _T_11 = x >= 9'h167; // @[VideoTiming.scala 114:17]
  wire  _T_12 = x < 9'h17e; // @[VideoTiming.scala 114:45]
  wire  _T_13 = y >= 9'h10f; // @[VideoTiming.scala 115:17]
  wire  _T_14 = y < 9'h111; // @[VideoTiming.scala 115:45]
  wire  _T_15 = x >= 9'h22; // @[VideoTiming.scala 118:20]
  wire  _T_16 = x < 9'h162; // @[VideoTiming.scala 118:51]
  wire  hDisplay = _T_15 & _T_16; // @[VideoTiming.scala 118:46]
  wire  _T_17 = y >= 9'h13; // @[VideoTiming.scala 119:20]
  wire  _T_18 = y < 9'h103; // @[VideoTiming.scala 119:51]
  wire  vDisplay = _T_17 & _T_18; // @[VideoTiming.scala 119:46]
  assign io_video_enable = hDisplay & vDisplay; // @[VideoTiming.scala 127:19]
  assign io_video_pos_x = x - 9'h22; // @[VideoTiming.scala 122:16]
  assign io_video_pos_y = y - 9'h13; // @[VideoTiming.scala 122:16]
  assign io_video_hSync = _T_11 & _T_12; // @[VideoTiming.scala 123:18]
  assign io_video_vSync = _T_13 & _T_14; // @[VideoTiming.scala 124:18]
  assign io_video_hBlank = ~hDisplay; // @[VideoTiming.scala 125:19]
  assign io_video_vBlank = ~vDisplay; // @[VideoTiming.scala 126:19]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  x = _RAND_0[8:0];
  _RAND_1 = {1{`RANDOM}};
  y = _RAND_1[8:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      x <= 9'h0;
    end else if (xWrap) begin
      x <= 9'h0;
    end else begin
      x <= _T_2;
    end
    if (reset) begin
      y <= 9'h0;
    end else if (xWrap) begin
      if (_T_4) begin
        y <= 9'h0;
      end else begin
        y <= _T_6;
      end
    end
  end
endmodule
module TestPattern(
  input        clock,
  input        reset,
  output       io_video_enable,
  output [8:0] io_video_pos_x,
  output [8:0] io_video_pos_y,
  output       io_video_hSync,
  output       io_video_vSync,
  output       io_video_hBlank,
  output       io_video_vBlank,
  output [3:0] io_rgb_r,
  output [3:0] io_rgb_g,
  output [3:0] io_rgb_b
);
  wire  videoTiming_clock; // @[TestPattern.scala 62:27]
  wire  videoTiming_reset; // @[TestPattern.scala 62:27]
  wire  videoTiming_io_video_enable; // @[TestPattern.scala 62:27]
  wire [8:0] videoTiming_io_video_pos_x; // @[TestPattern.scala 62:27]
  wire [8:0] videoTiming_io_video_pos_y; // @[TestPattern.scala 62:27]
  wire  videoTiming_io_video_hSync; // @[TestPattern.scala 62:27]
  wire  videoTiming_io_video_vSync; // @[TestPattern.scala 62:27]
  wire  videoTiming_io_video_hBlank; // @[TestPattern.scala 62:27]
  wire  videoTiming_io_video_vBlank; // @[TestPattern.scala 62:27]
  wire  _T_1 = videoTiming_io_video_pos_x[2:0] == 3'h0; // @[TestPattern.scala 67:27]
  wire  _T_3 = videoTiming_io_video_pos_y[2:0] == 3'h0; // @[TestPattern.scala 67:55]
  wire  _T_4 = _T_1 | _T_3; // @[TestPattern.scala 67:35]
  wire [5:0] _T_5 = _T_4 ? 6'h3f : 6'h0; // @[TestPattern.scala 67:8]
  wire [5:0] _T_7 = videoTiming_io_video_pos_x[4] ? 6'h3f : 6'h0; // @[TestPattern.scala 68:8]
  wire [5:0] _T_9 = videoTiming_io_video_pos_y[4] ? 6'h3f : 6'h0; // @[TestPattern.scala 69:8]
  wire [3:0] rgb_r = _T_5[3:0]; // @[RGB.scala 107:19 RGB.scala 108:11]
  wire [3:0] rgb_g = _T_7[3:0]; // @[RGB.scala 107:19 RGB.scala 109:11]
  wire [3:0] rgb_b = _T_9[3:0]; // @[RGB.scala 107:19 RGB.scala 110:11]
  VideoTiming videoTiming ( // @[TestPattern.scala 62:27]
    .clock(videoTiming_clock),
    .reset(videoTiming_reset),
    .io_video_enable(videoTiming_io_video_enable),
    .io_video_pos_x(videoTiming_io_video_pos_x),
    .io_video_pos_y(videoTiming_io_video_pos_y),
    .io_video_hSync(videoTiming_io_video_hSync),
    .io_video_vSync(videoTiming_io_video_vSync),
    .io_video_hBlank(videoTiming_io_video_hBlank),
    .io_video_vBlank(videoTiming_io_video_vBlank)
  );
  assign io_video_enable = videoTiming_io_video_enable; // @[TestPattern.scala 73:12]
  assign io_video_pos_x = videoTiming_io_video_pos_x; // @[TestPattern.scala 73:12]
  assign io_video_pos_y = videoTiming_io_video_pos_y; // @[TestPattern.scala 73:12]
  assign io_video_hSync = videoTiming_io_video_hSync; // @[TestPattern.scala 73:12]
  assign io_video_vSync = videoTiming_io_video_vSync; // @[TestPattern.scala 73:12]
  assign io_video_hBlank = videoTiming_io_video_hBlank; // @[TestPattern.scala 73:12]
  assign io_video_vBlank = videoTiming_io_video_vBlank; // @[TestPattern.scala 73:12]
  assign io_rgb_r = videoTiming_io_video_enable ? rgb_r : 4'h0; // @[TestPattern.scala 74:10]
  assign io_rgb_g = videoTiming_io_video_enable ? rgb_g : 4'h0; // @[TestPattern.scala 74:10]
  assign io_rgb_b = videoTiming_io_video_enable ? rgb_b : 4'h0; // @[TestPattern.scala 74:10]
  assign videoTiming_clock = clock;
  assign videoTiming_reset = reset;
endmodule
