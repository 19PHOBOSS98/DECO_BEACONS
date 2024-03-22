<div align="center">
  <h1>DECO BEACONS</h1>
  <img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/71ce350d-d828-468e-a4d4-5d6a4bcb0e7a" alt="DECO BEACONS" height="500">
  <h1></h1>
</div>

<div align="center">
  <p>Adds decorative beacons</p>
  <p>This is my test mod when I started learning about Minecraft modding to prep building <a href="https://github.com/19PHOBOSS98/Mirage/tree/1.18.2-forge">MIRAGE</a></p>
  
</div>



<div align="center">
  <h1>BLOCKS</h1>
  
  <img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/2a588f58-f141-4607-abad-4d78185d36e1" alt="DECO BEACON" height="200">
  </br>
  <span>Lights up when given redstone signal</span>
  </br>
  <span>Beam collides with block</span>
  </br>
  <h2>DECO BEACON</h2>
  
  <img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/2a8f7238-4c9d-47c4-bae1-60c467382df7" alt="GHOST DECO BEACON" height="200">
  </br>
  <span>Beam passes thru blocks</span>
  </br>
  <span>Block has no collision itself</span>
  </br>
  <h2>GHOST DECO BEACON</h2>
  
  <img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/2f857792-c49f-4360-af3e-39274912d479" alt="OMNI BEACON" height="200">
  </br>
  <span>Beam can be redirected</span>
  </br>
  <span>also has ghost variant</span>
  <h2>OMNI BEACON</h2>
 
  <img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/d62bda3b-c02f-4d61-b6a2-1e96f19304b4" alt="FAKE BEACON" height="200">
  </br>
  <span>Looks like an actual beacon...</span>
  </br>
  <span>Also has ghost variant</span>
  </br>
  <h2>FAKE BEACON</h2>
</div>
<div align="justify">
  <h1 align="center">HOW TO USE</h1>
    <h3>ON/OFF</h3>
      <p> give it a redstone signal to activate the beacon. Toggle ActiveLow mode by R-Clicking it with a redstone torch </p>
    <h3>PICK COLOR</h3>
      <p> Cycle thru the colors by R-Clicking the beacon with an empty hand. Shift R-Clicking it cycles thru the colors in reverse. R-Clicking it with a dye changes the color. It does not consume the dye. </p>
    <h3>LIGHTBEAM OPACITY</h3>
      <p> DecoBeacon light beams can pass thru other decobeacons and change the beams color. R-clicking with a Sand block makes the surface abrasive and stops the light beam from passing thru. Applying sand to a ghost beacon doesn't stop any light beam from passing thru however it does stop the ghost beacon from affecting the light beam's color</p>
    <h3>GHOSTS</h3>
      <p> Each beacon has a ghost variant. They work the same way with a few exceptions. Light beams from Ghost beacons pass thru blocks (except opaque beacons). As "ghosts" these blocks don't have collision letting you walk thru them. </p>
</div>

### BOOK SETTINGS

We can write down beacon settings in a Book-&-Quill and apply it to a beacon by R-Clicking.
Make sure to separate each setting with a comma "," and recheck what we type in.
The beacon will "calmly" point out what we did wrong

### General Settings:
- ### activeLow:true/false
  * if set to true, the beacon is "ACTIVE" when the redstone signal you give it is "LOW"
- ### isTransparent:true/false
  * if set to false, decobeacon light beams cannot pass thru this decobeacon... except if this decobeacon is a ghost variant.
  * setting this to false on a ghost variant beacon stops the beacon from affecting the passing light beam's color 
- ### color:white/orange/magenta/light_blue/yellow/lime/pink/gray/light_gray/cyan/purple/blue/brown/green/red/black
  * set beacon color

### OmniBeacon Exclusive Settings:
The following settings only work with omni beacons

- ### direction: north/south/east/west/up/down
  * sets the direction of the light beam
                
- ### maxBeamLenght: integer
  * sets the max length of the light beam


<div align="left">
      <h3>Examples:</h3>
         <p align="center"> DECOBEACON SETTINGS:<br><img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/21e23ce8-c932-4c8c-9263-b32326177540" alt="DECOBEACON SETTINGS" height="500"></p>
      </br>
          <p align="center"> OMNIBEACON SETTINGS:<br><img src="https://github.com/19PHOBOSS98/DECO_BEACONS/assets/37253663/0349913c-da2c-4fef-a00e-29cf505c4f49" alt="OMNIBEACON SETTINGS" height="500"></p>
      </br>
</div>





