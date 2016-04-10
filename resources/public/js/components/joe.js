C("actor", {},
 {mount: function(c){
    c.owner.transform.x = Math.random() * WIDTH;
    c.owner.transform.y = Math.random() * HEIGHT; },
  update: function(c){
   if (c.owner.transform.x > WIDTH){c.owner.transform.x = 0}
   if (c.owner.transform.x < 0){c.owner.transform.x = WIDTH}
   if (c.owner.transform.y > WIDTH){c.owner.transform.y = 0}
   if (c.owner.transform.y < 0){c.owner.transform.y = HEIGHT}}})


C("wander", {speed:2},
 {init: function(c){
    c.vx = (Math.random()-0.5) * 3;
    c.vy = (Math.random()-0.5) * 3;},
  update: function(c){
    c.vx = std.cap(c.vx + (Math.random()- 0.5) * 0.1, -3, 3)
    c.vy = std.cap(c.vy + (Math.random()- 0.5) * 0.1, -3, 3)
    c.owner.transform.x += c.vx;
    c.owner.transform.y += c.vy; }});


C("oscillate", 
 {axis:"x", speed:10, distance:10},
 {update: function(c) {
     c.owner.transform[c.axis] += Math.sin(new Date() * 0.01 / c.speed) * (c.distance * .1);}})


C("grid",
 {w:TILEWIDTH,
  h:TILEHEIGHT,
  datatype: Array,
  data: false,
  notfound: false,
  get: function(x, y){
    var c = this;
    var idx = (y - 1) * c.w + x;
    var found = c.data[idx];
    if (found == undefined){return c.notfound;} else {return c.data[idx];} },
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

