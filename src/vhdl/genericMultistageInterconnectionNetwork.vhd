library ieee;
use work.all;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;



entity genericMultistageInterconnectionNetwork is
    generic(
        N : natural := 8;
        messageW : natural := 32;
        D : natural := 32
        );
    
    port(
        clk : in std_logic;
        reset : in std_logic;
        messageDin : in std_logic_vector(N*messageW - 1 downto 0);
        destcodeDin : in std_logic_vector(N*N - 1 downto 0);
        validDin : in std_logic_vector(N - 1 downto 0);
        nFullDin : in std_logic_vector(N - 1 downto 0);
        
        messageDout : out std_logic_vector(N*messageW - 1 downto 0);
        validDout : out std_logic_vector(N - 1 downto 0);
        nFullDout : out std_logic_vector(N - 1 downto 0)
        );
end genericMultistageInterconnectionNetwork;


architecture arch of genericMultistageInterconnectionNetwork is
    
    constant nSwitchPerStage : integer := N/2;
    constant nStage : integer := integer(log2(real(N)));
    constant nSwitch : integer := nSwitchPerStage * nStage;
    
    type messageArray is array((nStage+1)*nSwitchPerStage*2 - 1 downto 0) of std_logic_vector(messageW - 1 downto 0);
    type destcodeArray is array((nStage+1)*nSwitchPerStage*2 - 1 downto 0) of std_logic_vector(N - 1 downto 0);
    
    signal messageSignals : messageArray;
    signal destcodeSignals : destcodeArray;
    signal validSignals : std_logic_vector((nStage+1)*nSwitchPerStage*2 - 1 downto 0);
    signal nFullSignals : std_logic_vector((nStage+1)*nSwitchPerStage*2 - 1 downto 0);
    
begin
    
    Stage_gen : for stage in 0 to nStage - 1 generate
        
        constant nBlock : integer := 2**stage;
        constant nSwitchPerBlock : integer := nSwitchPerStage / nBlock;
        begin
        
        Block_gen : for blockIdx in 0 to (2**stage) - 1 generate
            Switch_gen : for switchIdx in 0 to nSwitchPerStage/(2**stage) - 1 generate
                
                constant switchGlobalIdx : integer := switchIdx + nSwitchPerStage*stage + blockIdx*nSwitchPerBlock;
                constant offset : integer := 2*nSwitchPerStage;
                
                begin
                
                Switch_top : if ((switchIdx >= 0 and switchIdx < N/(4*2**stage)) or stage = (integer(log2(real(N))) - 1))  generate
                    
                    constant upperOutIdx : integer := 2*switchGlobalIdx + offset;
                    constant lowerOutIdx : integer := 2*switchGlobalIdx + nSwitchPerBlock + offset;
                    
                    begin  
                    st: entity switch(arch)
                    generic map(
                        dest_code_W => N/2**stage,
                        message_W => messageW,
                        input_W => (N/2**stage) + messageW,
                        D => D
                        )
                    port map(
                        message_0 => messageSignals(2*switchGlobalIdx),
                        message_1 => messageSignals(2*switchGlobalIdx + 1),
                        
                        dest_code_0 => destcodeSignals(2*switchGlobalIdx)(N/2**stage - 1 downto 0),
                        dest_code_1 => destcodeSignals(2*switchGlobalIdx + 1)(N/2**stage - 1 downto 0),
                        
                        valid_0 => validSignals(2*switchGlobalIdx),
                        valid_1 => validSignals(2*switchGlobalIdx + 1),
                        
                        reset => reset,
                        clk => clk,
                        
                        n_full_in_upper => nFullSignals(upperOutIdx),
                        n_full_in_lower => nFullSignals(lowerOutIdx),
                        
                        message_upper_out => messageSignals(upperOutIdx),
                        message_lower_out => messageSignals(lowerOutIdx),
                        
                        dest_code_upper_out => destcodeSignals(upperOutIdx)((N/2**(stage+1)) - 1 downto 0),
                        dest_code_lower_out => destcodeSignals(lowerOutIdx)((N/2**(stage+1)) - 1 downto 0),
                        
                        valid_upper_out => validSignals(upperOutIdx),
                        valid_lower_out => validSignals(lowerOutIdx),
                        
                        n_full_out_upper => nFullSignals(2*switchGlobalIdx),
                        n_full_out_lower => nFullSignals(2*switchGlobalIdx + 1)
                        );
                    
                end generate; 
                Switch_bottom : if switchIdx >= N/(4*2**stage) and switchIdx < N/(2*2**stage) and stage /= (integer(log2(real(N))) - 1) generate
                    
                    constant upperOutIdx : integer := 2*switchGlobalIdx + 1 - nSwitchPerBlock + offset;
                    constant lowerOutIdx : integer := 2*switchGlobalIdx + 1 + offset;
                    
                    begin
                    sb: entity switch(arch)
                    generic map(
                        dest_code_W => N/2**stage,
                        message_W => messageW,
                        input_W => (N/2**stage) + messageW,
                        D => D
                        )
                    port map(
                        message_0 => messageSignals(2*switchGlobalIdx),
                        message_1 => messageSignals(2*switchGlobalIdx + 1),
                        
                        dest_code_0 => destcodeSignals(2*switchGlobalIdx)(N/2**stage - 1 downto 0),
                        dest_code_1 => destcodeSignals(2*switchGlobalIdx + 1)(N/2**stage - 1 downto 0),
                        
                        valid_0 => validSignals(2*switchGlobalIdx),
                        valid_1 => validSignals(2*switchGlobalIdx + 1),
                        
                        reset => reset,
                        clk => clk,
                        
                        n_full_in_upper => nFullSignals(upperOutIdx),
                        n_full_in_lower => nFullSignals(lowerOutIdx),
                        
                        message_upper_out => messageSignals(upperOutIdx),
                        message_lower_out => messageSignals(lowerOutIdx),
                        
                        dest_code_upper_out => destcodeSignals(upperOutIdx)((N/2**(stage+1)) - 1 downto 0),
                        dest_code_lower_out => destcodeSignals(lowerOutIdx)((N/2**(stage+1)) - 1 downto 0),
                        
                        valid_upper_out => validSignals(upperOutIdx),
                        valid_lower_out => validSignals(lowerOutIdx),
                        
                        n_full_out_upper => nFullSignals(2*switchGlobalIdx),
                        n_full_out_lower => nFullSignals(2*switchGlobalIdx + 1)
                        );
                end generate;
            end generate;
        end generate;
    end generate;
    
    signal_assignment: for idx in 0 to N - 1 generate
        nFullDout(idx) <= nFullSignals(idx);
        validDout(idx) <= validSignals(idx + nStage*nSwitchPerStage*2);
        messageDout(messageW*(idx + 1) - 1 downto messageW*idx) <= messageSignals(idx + nStage*nSwitchPerStage*2);
        
        messageSignals(idx) <= messageDin(messageW*(idx + 1) - 1 downto messageW*idx);
        destcodeSignals(idx) <= destcodeDin(N*(idx + 1) - 1 downto N*idx);
        validSignals(idx) <= validDin(idx);
        nFullSignals(idx + nStage*nSwitchPerStage*2) <= nFullDin(idx);
    end generate; 
    
end arch;
