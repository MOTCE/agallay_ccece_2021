library ieee;
library work;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity fifo is
   generic (
      D : positive := 1024; -- la profondeur (le nombre d'éléments) dans la file
      W : positive := 8     -- la largeur (en bits) de la file
   );
   port (
      clk   : in std_logic;
      reset : in std_logic;                         -- actif haut: un '1' réinitialise la file
      din   : in std_logic_vector(W - 1 downto 0);  -- données entrant dans la file
      dout  : out std_logic_vector(W - 1 downto 0); -- données sortant de la file
      wr_en : in std_logic;                         -- write-enable: si actif, une donnée sera lue de din et
                                                    -- entrée dans la file au prochain front montant de clk
      rd_en : in std_logic;                         -- read-enable: si actif, une donnée sera sortie de la file
                                                    -- et placée sur dout au prochain front montant de clk
      empty : out std_logic;                        -- indique que la file est vide
      full  : out std_logic                         -- indique que la file est pleine
   );
end fifo;

architecture arch of fifo is

   signal nb_values : natural range 0 to D   := 0;
   signal read_add  : natural range 0 to D-1 := 0;
   signal write_add : natural range 0 to D-1 := 0;	
   type memtype is array( 0 to D-1 ) of std_logic_vector( W-1 downto 0 );
   signal memory : memtype;
   
begin
   
   process( clk, reset) is
   begin
   
      if( rising_edge( clk ) ) then
         if( reset = '1' ) then
         
            nb_values <= 0;
            read_add <= 0;
            write_add <= 0;
            
         else
         
            if( wr_en = '1' and rd_en = '0' and (nb_values < D) ) then
               nb_values <= nb_values + 1;
            elsif( wr_en = '0' and rd_en = '1' and (nb_values > 0) ) then
               nb_values <= nb_values - 1;
            end if;
            
            if ( wr_en = '1' ) then
               if( (nb_values < D) or (nb_values = D and rd_en = '1') ) then
               
                  if( write_add = D-1 ) then
                     write_add <= 0;
                  else
                     write_add <= write_add + 1;
                  end if;
                  
                  memory( write_add ) <= din;
                  
               end if;
            end if;        
            
            if ( rd_en = '1' ) then
               if( nb_values > 0 ) then
                  
                  if( read_add = D-1 ) then
                     read_add <= 0;
                  else
                     read_add <= read_add + 1;
                  end if;
                  
                  dout <= memory( read_add );
                  
               end if;
            end if;
            
         end if;
      end if;
   
   end process;
   
   full <= '1' when ( nb_values = D ) else '0';
   empty <= '1' when ( nb_values = 0 ) else '0';

end arch;