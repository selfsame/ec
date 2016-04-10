C('movie', {
  frames: [],
  animationSpeed: .5,
  anchor: .5,
  frameCount: 1,
  frameName: '',
  paused: false
}, {
  mount: function(c) {
    for (var i = 0; i < c.frameCount; i++) {
      var f = ('0000' + i).substr(-4);
      c.frames.push(PIXI.Texture.fromFrame(c.frameName + '.' + f + '.png'));
    }

    c.instance = new PIXI.extras.MovieClip(c.frames);
    c.instance.anchor.set(c.anchor);
    c.instance.animationSpeed = c.animationSpeed;
    root.pixi.stage.addChild(c.instance);
  },
  update: function(c) {
    c.instance.rotation = c.owner.transform.rotation;
    c.instance.x = c.owner.transform.position.x;
    c.instance.y = c.owner.transform.position.y;
    c.instance.play();

  }
});