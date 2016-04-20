
white = new THREE.MeshLambertMaterial({color:0xdddddd})
box = new THREE.BoxGeometry( 200, 200, 200 )
sphere = new THREE.SphereGeometry(70,32,16)

C("renderer",
 {w:1500, h:900},
 {init: function(c){
    c.i = new THREE.WebGLRenderer()
    c.i.setSize(c.w, c.h)},
  mount: function(c){
    c.scene = c.owner.findComponents("scene")[0].i
    c.camera = c.owner.findComponents("camera")[0].i
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
    c.i.add(new THREE.DirectionalLight(0xffffff, 0.725))
  }})

C("camera", 
 {fov:75, aspect:2, near: 1, far:1000},
 {init: function(c){
    c.i = new THREE.PerspectiveCamera(c.fov, c.aspect, c.near, c.far);
    c.i.position.z = 600;},
  mount: function(c){
    var r = c.owner.findComponents("renderer");
    c.i.aspect = r.w/r.h;},
  update: function(c){}})

C("mesh", 
 {geometry:sphere},
 {init: function(c){
    c.i = new THREE.Mesh(c.geometry, (c.material || white))},
  mount: function(c){
    var s = c.owner.findAncestorComponents("scene")[0] || 
      c.owner.findComponents("scene")[0];
    xx = c;
    if (s){s.i.add(c.i)}}})


// C("transform",
//   {},
//  {expose: ["position","scale","rotation","parent","children","visible","alpha"],
//   mount: function(c) {
//     if (c.owner.owner){
//       c.owner.owner.transform.addChild(c);}},
//   unmount: function(c) {
//     if (c.parent){c.parent.removeChild(c.instance);}}})


// C("sprite",
//  {image: "", alpha:1.0, x:0, y:0 },
//  {init: function(c){},
//   mount: function(c){
//     if (c.image){
//       c.instance = new PIXI.Sprite.fromImage(c.image);;} 
//     else {
//       c.instance = new PIXI.Sprite();}
//     c.instance.x = c.x; c.instance.y = c.y;
//     c.instance.alpha = c.alpha;
//     c.owner.transform.addChild(c.instance);},
//   unmount: function(c){
//     c.instance.parent.removeChild(c.instance);}})


// C("text",
//  {text:"hello world",
//   font : '24px Arial',
//   fill : 0x000000,
//   align : 'center',
//   x:0, y:0},
//  {init: function(c){
//     c.instance = new PIXI.Text(c.text, c);
//     c.instance.x = c.x;
//     c.instance.y = c.y;},
//   mount: function(c){
//     c.owner.transform.addChild(c.instance);},
//   unmount: function(c){
//     c.instance.parent.removeChild(c.instance);}})


// C("rect",
//  {x:0, y:0, 
//   w:0, h:0,
//   fill : 0xffffff,
//   alpha: 1.0},
//  {init: function(c){
//     c.instance = new PIXI.Graphics();
//     c.instance.beginFill(c.fill, c.alpha);
//     c.instance.drawRect(c.x, c.y, c.w, c.h);
//     c.instance.endFill();},
//   mount: function(c){
//     c.owner.transform.addChild(c.instance);},
//   unmount: function(c){
//     c.instance.parent.removeChild(c.instance);}})


// C("pivotgizmo",
//  {fill : 0xff0000,
//   alpha: 1.0,
//   radius: 10},
//  {init: function(c){
//     c.instance = new PIXI.Graphics;
//     c.instance.beginFill(c.fill, c.alpha);
//     c.instance.drawCircle(c.x, c.y, c.radius);
//     c.instance.endFill();},
//   mount: function(c){
//     c.owner.transform.addChild(c.instance);},
//   update: function(c){
//     c.instance.x = c.owner.transform.pivot.x;
//     c.instance.y = c.owner.transform.pivot.y;
//     c.owner.transform.setChildIndex(c.instance, c.owner.transform.children.length - 1);},
//   unmount: function(c){
//     c.instance.parent.removeChild(c.instance);}})


// C("drawsort",
//  {fn:function(a, b){ return a.position.y - b.position.y; }},
//  {update:
//   function(c){
//     c.owner.transform.children.sort(c.fn);}})



