library ieee;
library work;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity fork is
    
    generic(
    message_W : natural := 8;
    dest_code_W: natural := 8
    );
    
    port(
    
    message_in : in std_logic_vector(message_W - 1 downto 0);
    dest_code_in : in std_logic_vector(dest_code_W - 1 downto 0);
    valid_in : in std_logic;
    
    message_upper : out std_logic_vector(message_W - 1 downto 0);
    dest_code_upper : out std_logic_vector(dest_code_W/2 - 1 downto 0);
    valid_upper : out std_logic;
    
    message_lower : out std_logic_vector(message_W - 1 downto 0);
    dest_code_lower : out std_logic_vector(dest_code_W/2 - 1 downto 0);
    valid_lower : out std_logic
    
    );
    
end fork;

architecture arch of fork is


begin
    
    valid_upper <= valid_in and or(dest_code_in(dest_code_W/2 - 1 downto 0));    
    valid_lower <= valid_in and or(dest_code_in(dest_code_W - 1 downto dest_code_W / 2));

    message_upper <= message_in;
    message_lower <= message_in;
    
    dest_code_upper <= dest_code_in(dest_code_W/2 - 1 downto 0);
    dest_code_lower <= dest_code_in(dest_code_W - 1 downto dest_code_W/2);
    
end arch;
