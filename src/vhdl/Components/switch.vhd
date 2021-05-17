library ieee;
library work;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

use work.all;

entity switch is
    generic(
        dest_code_W : natural := 8;
        message_W : natural := 8;
        input_W : natural := dest_code_W + message_W;
        D : natural := 1024
        );
    
    port(
        message_0 : in std_logic_vector(message_W - 1 downto 0);
        message_1 : in std_logic_vector(message_W - 1 downto 0);
        
        dest_code_0 : in std_logic_vector(dest_code_W - 1 downto 0);
        dest_code_1 : in std_logic_vector(dest_code_W - 1 downto 0);
        
        valid_0 : in std_logic;
        valid_1 : in std_logic;
        
        reset : in std_logic;
        clk : in std_logic;
        
        n_full_in_upper : in std_logic;
        n_full_in_lower : in std_logic;
         
        message_upper_out : out std_logic_vector(message_W - 1 downto 0);
        message_lower_out : out std_logic_vector(message_W - 1 downto 0);
        
        dest_code_upper_out : out std_logic_vector((dest_code_W/2) - 1 downto 0);
        dest_code_lower_out : out std_logic_vector((dest_code_W/2) - 1 downto 0);
        
        valid_upper_out : out std_logic;
        valid_lower_out : out std_logic;
        
        n_full_out_upper : out std_logic;
        n_full_out_lower : out std_logic
        );
end switch;

architecture arch of switch is
    
    -- Signaux FORK superieure
    
    signal message_00 : std_logic_vector(message_W - 1 downto 0);
    signal message_01 : std_logic_vector(message_W - 1 downto 0);
    
    signal dest_code_00 : std_logic_vector((dest_code_W/2) - 1 downto 0);
    signal dest_code_01 : std_logic_vector((dest_code_W/2) - 1 downto 0);
    
    signal valid_00 : std_logic;
    signal valid_01 : std_logic;
    
    --
    
    -- Signaux FORK inferieure
    
    signal message_10 : std_logic_vector(message_W - 1 downto 0);
    signal message_11 : std_logic_vector(message_W - 1 downto 0);
    
    signal dest_code_10 : std_logic_vector((dest_code_W/2) - 1 downto 0);
    signal dest_code_11 : std_logic_vector((dest_code_W/2) - 1 downto 0);
    
    signal valid_10 : std_logic;
    signal valid_11 : std_logic;
    
    --
    
    -- Signaux fifo_upper_0
    
    signal concat_00 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    
    signal dout_00 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    signal full_00 : std_logic;
    
    signal empty_00 : std_logic := '1';
    
    --
    
    -- Signaux fifo_upper_1
    
    signal concat_01 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    
    signal dout_01 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    signal full_01 : std_logic;
    
    signal empty_01 : std_logic := '1';
    
    --
    
    -- Signaux fifo_lower_0
    
    signal concat_10 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    
    signal dout_10 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    signal full_10 : std_logic;
    
    signal empty_10 : std_logic := '1';
    
    -- Signaux fifo_lower_0
    
    signal concat_11 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    
    signal dout_11 : std_logic_vector(message_W + (dest_code_W/2) - 1 downto 0);
    signal full_11 : std_logic;
    
    signal empty_11 : std_logic := '1';
    
    --
    
    -- Signaux selector_upper
    
    signal re_00 : std_logic;
    signal re_01 : std_logic;
    
    -- Signaux selector_lower
    
    signal re_10 : std_logic;
    signal re_11 : std_logic;
    
    --
    
    function concat(message_in : in std_logic_vector(message_W - 1 downto 0); dest_code_in : in std_logic_vector((dest_code_W / 2) - 1 downto 0))
        return std_logic_vector is variable concat_return : std_logic_vector(message_W + dest_code_W/2 - 1 downto 0);
    begin
        concat_return(dest_code_W/2 + message_W - 1 downto message_W) := dest_code_in;
        concat_return(message_W - 1 downto 0) := message_in;
        
        return concat_return;
    end;
    
    
begin
    
    concat_00 <= concat(message_00, dest_code_00);
    concat_01 <= concat(message_01, dest_code_01);
    
    concat_10 <= concat(message_10, dest_code_10);
    concat_11 <= concat(message_11, dest_code_11);
    
    
    n_full_out_upper <= not(full_00 or full_01);
    n_full_out_lower <= not(full_10 or full_11);

    
    -- FORK UPPER --
    
    fork_upper : entity fork(arch)
    generic map(
        message_W => message_W,
        dest_code_W => dest_code_W
        )
    port map(
        message_in => message_0,
        dest_code_in => dest_code_0,
        valid_in => valid_0,
        
        message_upper => message_00,
        dest_code_upper => dest_code_00,
        valid_upper => valid_00,
        
        message_lower => message_01,
        dest_code_lower => dest_code_01,
        valid_lower => valid_01
        );
    
    -- FORK LOWER --
    
    fork_lower : entity fork(arch)
    generic map(
        message_W => message_W,
        dest_code_W => dest_code_W
        )
    port map(
        message_in => message_1,
        dest_code_in => dest_code_1,
        valid_in => valid_1,
        
        message_upper => message_10,
        dest_code_upper => dest_code_10,
        valid_upper => valid_10,
        
        message_lower => message_11,
        dest_code_lower => dest_code_11,
        valid_lower => valid_11
        );
    
    -- FIFOs upper
    
    fifo_upper_0 : entity fifo(arch)
    generic map(
        D => D,
        W => message_W + (dest_code_W/2)
        )
    port map(
        clk => clk,
        reset => reset,
        din => concat_00,
        dout => dout_00,
        wr_en => valid_00,
        rd_en => re_00,
        empty => empty_00,
        full => full_00
        );
    
    fifo_upper_1 : entity fifo(arch)
    generic map(
        D => D,
        W => message_W + (dest_code_W/2)
        )
    port map(
        clk => clk,
        reset => reset,
        din => concat_01,
        dout => dout_01,
        wr_en => valid_01,
        rd_en => re_10,
        empty => empty_01,
        full => full_01
        );
    
    ------------------------------------
    
    -- FIFOs lower
    
    fifo_lower_0 : entity fifo(arch)
    generic map(
        D => D,
        W => message_W + (dest_code_W/2)
        )
    port map(
        clk => clk,
        reset => reset,
        din => concat_10,
        dout => dout_10,
        wr_en => valid_10,
        rd_en => re_01,
        empty => empty_10,
        full => full_10
        );
    
    fifo_lower_1 : entity fifo(arch)
    generic map(
        D => D,
        W => message_W + (dest_code_W/2)
        )
    port map(
        clk => clk,
        reset => reset,
        din => concat_11,
        dout => dout_11,
        wr_en => valid_11,
        rd_en => re_11,
        empty => empty_11,
        full => full_11
        );
    
    ------------------------------------
    
    
    -- SELECTORs
    
    selector_upper : entity selector(arch)
    generic map(
        dest_code_width => dest_code_W/2,
        message_width => message_W,
        width_in => dest_code_W/2 + message_W
        )
    port map(
        clk => clk,
        reset => reset,
        n_full => n_full_in_upper,
        upper_din => dout_00,
        lower_din => dout_10,
        upper_empty => empty_00,
        lower_empty => empty_10,
        upper_re => re_00,
        lower_re => re_01,
        valid_out => valid_upper_out,
        dest_code_out => dest_code_upper_out,
        message_out => message_upper_out
        );
    
    selector_lower : entity selector(arch)
    generic map(
        dest_code_width => dest_code_W/2,
        message_width => message_W,
        width_in => dest_code_W/2 + message_W
        )
    port map(
        clk => clk,
        reset => reset,
        n_full => n_full_in_lower,
        upper_din => dout_01,
        lower_din => dout_11,
        upper_empty => empty_01,
        lower_empty => empty_11,
        upper_re => re_10,
        lower_re => re_11,
        valid_out => valid_lower_out,
        dest_code_out => dest_code_lower_out,
        message_out => message_lower_out
        );
    
    ------------------------------------
end arch;
