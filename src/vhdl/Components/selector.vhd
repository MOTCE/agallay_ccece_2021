library ieee;
library work;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity selector is
    
    generic(
        
        dest_code_width : natural := 4;
        message_width : natural := 8;
        width_in : natural := dest_code_width + message_width
        
        );
    
    port(
        
        clk : in std_logic;
        reset : in std_logic;
        
        n_full : in std_logic;
        
        upper_din : in std_logic_vector(width_in - 1 downto 0);
        lower_din : in std_logic_vector(width_in - 1 downto 0);
        
        upper_empty : in std_logic;
        lower_empty : in std_logic;
        
        upper_re : out std_logic;
        lower_re : out std_logic;
        
        valid_out : out std_logic;
        dest_code_out : out std_logic_vector(dest_code_width - 1 downto 0);
        message_out : out std_logic_vector(message_width - 1 downto 0)
        
        
        );
    
end selector;

architecture arch of selector is
    
    signal priority : std_logic := '0';
    
    signal valid : std_logic := '0';
    signal re : std_logic;
    
    signal upperPortRead : std_logic;
    signal lowerPortRead : std_logic;
    
    signal upper_re_signal : std_logic;
    signal lower_re_signal : std_logic;
    
    signal select_signal : std_logic_vector(2 downto 0);
    
begin
    
    process(clk)
    begin
        
        if rising_edge(clk) then
            
            if reset = '1' then
                priority <= '1';
                valid <= '0';
                upperPortRead <= '0';
                lowerPortRead <= '0';
            else
                if upper_empty = '0' and lower_empty = '0' and n_full = '1' then
                    priority <= not(priority);
                end if;
                
                valid <= re;
                upperPortRead <= upper_re_signal; 
                lowerPortRead <= lower_re_signal;
                
            end if;
        end if;
        
    end process;
    
    select_signal <= n_full & upperPortRead & lowerPortRead;
    
    with select_signal select message_out <=
    lower_din(message_width - 1 downto 0) when "101",
    upper_din(message_width - 1 downto 0) when "110",
    (message_out'length - 1 downto 0 => '0') when others;
    
    with select_signal select dest_code_out <=
    lower_din(width_in - 1 downto (width_in - dest_code_width)) when "101",
    upper_din(width_in - 1 downto (width_in - dest_code_width)) when "110",
    (dest_code_out'length - 1 downto 0 => '0') when others;
    
    valid_out <= valid;
    
    upper_re_signal <= n_full and not(upper_empty) and (not(priority) or lower_empty);
    lower_re_signal <= n_full and not(lower_empty) and (priority or upper_empty);
    
    upper_re <= upper_re_signal;
    lower_re <= lower_re_signal;
    
    re <= upper_re or lower_re;
    
end arch;
