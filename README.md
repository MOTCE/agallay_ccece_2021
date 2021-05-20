[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="images/logo.jpg" alt="Logo" width="380px" height="156px">
  </a>

  <h3 align="center">Multistage interconnection network (MIN) implementation using VHDL and Chisel, presented at the CCECE 2021 Conference.</h3>

  <p align="center">
    This project is the VHDL and Chisel implementation of a multistage interconnection network. The project was built within the scope of Polytechnique Montreal's UPIR scholarship award. 
  </p>
</p>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#contributors">Contributors</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

This project is organized as follow : src folder contains two folders. Each one contains src files of Chisel/VHDL implementation of the studied design. You can find the whole MIN in the .vhd / .scala file located at the root of the vhdl / chisel folder.

Components can be found inside the components folder.

### Built With

- [Vivado](https://www.xilinx.com/products/design-tools/vivado.html)

<!-- GETTING STARTED -->

## Getting Started

VHDL:
You should build a Vivado project and link every VHDL file to the project in order to compile the Multistage Interconnection Network module.

Chisel:
In order to compile and produce Verilog or write tests with given files, you should have installed all the requisites from https://www.chisel-lang.org/. You should pay attention to linking between .scala files since the repositories structure does not reflects the one used for development purposes.

### Prerequisites

Any VHDL compiler for .vhd files.
SBT for Scala compilation. IntelliJ is greatly recommended for development purposes.

### Installation

Chisel:
Please refer to https://github.com/schoeberl/chisel-book for more details about getting started into Chisel and https://github.com/freechipsproject/chisel-template for starting a simple Chisel project. You can drag and drop src files from this repo once you are done setting up a Chisel project. You will find useful command lines in Chisel Book.

## Usage

Use this project for educational purposes.

## Contributing

No contributions are expected for this project.

## License

MIT Licence - Copyright (c) 2021 MOTCE

## Contact

Andy GALLAY - andy.gallay@polymtl.ca

<!-- ACKNOWLEDGEMENTS -->

## Contributors
- [Pr. Tarek Ould Bachir (PhD)](https://www.researchgate.net/profile/Tarek-Ould-Bachir)

## Acknowledgements

- [Federico Montano (PhD candidate)](https://www.researchgate.net/profile/Federico-Montano)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/andygallay/
