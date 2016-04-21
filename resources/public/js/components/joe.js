C("position_wrap", 
  {x:1000, y: 1000, z:1000},
 {update: function(c){
   if (c.owner.transform.i.position.x >  c.x){c.owner.transform.i.position.x = -c.x}
   if (c.owner.transform.i.position.x < -c.x){c.owner.transform.i.position.x =  c.x}
   if (c.owner.transform.i.position.y >  c.y){c.owner.transform.i.position.y = -c.y}
   if (c.owner.transform.i.position.y < -c.y){c.owner.transform.i.position.y =  c.y}
   if (c.owner.transform.i.position.z >  c.z){c.owner.transform.i.position.z = -c.z}
   if (c.owner.transform.i.position.z < -c.z){c.owner.transform.i.position.z =  c.z}}})

C("wander", 
 {speed:2},
 {init: function(c){
    c.vx = (Math.random()-0.5) * 3 * c.speed;
    c.vz = (Math.random()-0.5) * 3 * c.speed;},
  update: function(c){
    c.vx = std.cap(c.vx + (Math.random()- 0.5) * 0.1, -3, 3)
    c.vz = std.cap(c.vz + (Math.random()- 0.5) * 0.1, -3, 3)
    c.owner.transform.i.position.x += c.vx
    c.owner.transform.i.position.z += c.vz}});

C("scatter", 
  {x:0, y:0, z: 0},
  {mount: function(c){
    c.owner.transform.i.position.x += Math.random()*(c.x*2) - c.x
    c.owner.transform.i.position.y += Math.random()*(c.y*2) - c.y
    c.owner.transform.i.position.z += Math.random()*(c.z*2) - c.z}})

C("oscillate", 
 {axis:"x", speed:10, distance:10},
 {update: function(c) {
    c.owner.transform.i.position[c.axis] += 
      x + Math.sin(new Date() * 0.01 / c.speed)
        * (c.distance * .1)}})

C("grid",
 {w:200, h:200,
  datatype: Array,
  data: false, 
  nf: false,
  get: function(x, y){
    var c = this;
    var idx = (y - 1) * c.w + x;
    var found = c.data[idx];
    if (found == undefined){return c.nf;} else {return c.data[idx];} },
  set: function(x, y, v){
    var c = this;
    var idx = (y - 1) * c.w + x;
    c.data[idx] = v; },
  mapindexed: function(f){
    var c = this;
    for(var i=0;i<c.data.length;i++){
      var y = parseInt(i/c.w);
      var x = i - (y*c.w);
      f(i, x, y); }}},
 {init: function(c){
  c.data = new c.datatype(c.w * c.h);}});




C("background",
  {o:5},
 {init: function(c){ },
  mount: function(c){
    c.source = c.owner.grid;

    c.source.mapindexed(function(i, x, y){
      c.source.data[i] = parseInt(Math.random()*15)});

    var base = new PIXI.extras.TilingSprite.fromImage("assets/place/terrain03.png", WIDTH, HEIGHT);
    c.owner.transform.addChild(base);

    var p = new PERLIN.Generator();
    p.octaves = 2
    p.frequency = 0.2
    p.persistance = .2
    c.instance = new PIXI.ParticleContainer()
    p.generate ([0,0], [c.source.w, c.source.h], function(point, value){
     var modval = parseInt((value * 8) + 5);
     if ( ((value > 0.5 && value < 0.9) &&
           ((point[0] % modval == 0 || point[0] % modval == 2) ||
            (point[1] % modval == 0 || point[1] % modval == 2))) ){
         var tile = new PIXI.Sprite(PIXI.Texture.fromFrame(c.source.data[5] + c.o));
         tile.position.x = point[0] * TILESIZE;
       tile.position.y = point[1] * TILESIZE;
       c.instance.addChild(tile);}})
    c.owner.transform.addChild(c.instance);}});


