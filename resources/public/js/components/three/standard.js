is = function(col, x){return (col.indexOf(x) != -1)}
choose = function(m){return m[parseInt(Math.random()*m.length)]}

white = new THREE.MeshLambertMaterial({color:0xdddddd})
box = new THREE.BoxGeometry( 40, 40, 40 )
sphere = new THREE.SphereGeometry(40,32,16)
plane = new THREE.PlaneGeometry(10000,10000)

width = window.innerWidth
height = window.innerHeight

C("renderer",
 {w:1500, h:900, shadowmap:true},
 {init: function(c){
    c.i = renderer = new THREE.WebGLRenderer();
    c.i.shadowMap.enabled = c.shadowmap;
    c.i.setSize(c.w, c.h)},
  mount: function(c){
    c.scene = c.owner.findComponents("scene")[0].i
    c.camera = (c.owner.findComponents("camera")[0] || c.owner.findComponents("orthocamera")[0]).i


      renderer.shadowCameraNear = 3;
      renderer.shadowCameraFar = c.camera.far;
      renderer.shadowCameraFov = 50;
      renderer.shadowMapBias = 0.0039;
      renderer.shadowMapDarkness = 0.5;

    document.body.appendChild(c.i.domElement)},
  update: function(c){
    if (c.camera){
      c.i.render(c.scene, c.camera)}}})

C("scene", 
 {},
 {init: function(c){
    scene = c
    c.i = new THREE.Scene;
    c.i.add(new THREE.AmbientLight(0x1d1d1d))

    light = new THREE.DirectionalLight( 0xffffff, 0.8);
    light.position.set( 0, 1000, 00 );
    light.target.position.set( 0, 0, 0 );
    light.castShadow = true;
    light.shadow = new THREE.DirectionalLightShadow( new THREE.PerspectiveCamera( 50, 1, 1200, 2500 ) );
    light.shadow.bias = 0.0001;
    light.shadow.mapSize.width = 2048;
    light.shadow.mapSize.height = 2048;
    light.castShadow = true;
    c.i.add(light)
  },
  mount:function(c){
    c.i.add(c.owner.transform.i)}})

C("camera", 
 {fov:45, aspect:width/height, near: 1, far:99000},
 {init: function(c){
    camera = c
    c.i = new THREE.PerspectiveCamera(c.fov, c.aspect, c.near, c.far);
    c.i.position.z = 2000;
    c.i.position.y = 1200;},
  mount: function(c){
    var r = c.owner.findComponents("renderer");
    c.i.aspect = r.w/r.h;
    c.target = c.owner},
  update: function(c){
    if (c.target){c.i.lookAt(c.target.transform.i.position)}
    var dx = 0
    if (is(KEYS, 37)) {dx -= 1}
    if (is(KEYS, 39)) {dx += 1}
    c.i.translateX(dx * 50)
    var dz = 0
    if (is(KEYS, 38)) {dz -= 1}
    if (is(KEYS, 40)) {dz += 1}
    c.i.translateZ(dz * 50)}})

C("orthocamera", 
 {},
 {init: function(c){
    camera = c
    c.i = new THREE.OrthographicCamera( width / - 1, width / 1, height / 1, height / - 1, 1, 9000 );
    c.i.position.y = 1200;
    c.i.rotation.x = -.7;}})

C("mesh", 
 {x:0,y:0,z:0},
 {init: function(c){
    c.i = new THREE.Mesh(c.geometry || sphere, (c.material || white))
    c.i.castShadow = true;
    c.i.recieveShadow = true;
    c.i.position.set(c.x, c.y, c.z)
    if (c.rotation){
      c.i.rotation.x = (c.rotation.x || c.i.rotation.x)
      c.i.rotation.y = (c.rotation.y || c.i.rotation.y)
      c.i.rotation.z = (c.rotation.z || c.i.rotation.z) }},
  mount: function(c){
    c.owner.transform.i.add(c.i);}})


C("transform", {},
 {init: function(c){c.i = new THREE.Object3D;},
  mount: function(c) {
    if (c.owner.owner){
      c.owner.owner.transform.i.add(c.i);}},
  unmount: function(c) {
    if (c.i.parent){c.i.parent.remove(c.i);}}})
