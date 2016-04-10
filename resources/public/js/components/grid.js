C('grid', {
  'width': constants.DEFAULT_MAP_WIDTH,
  'height': constants.DEFAULT_MAP_HEIGHT,

  /*
   * Default tile types
   *
   * `fill` is what default fill is
   * `ob` is what default out-of-bounds is
   */
  'defaults': {
    'fill': 0,
    'ob': 0
  },

  /*
   * The type of array this grid uses
   */
  'ArrayType': Array(),

  /*
   * The real 1d array of tiles
   */
  '_tiles': false,

  'init': function(c) {
    c._tiles = new c.type(c.width * c.height);
    c.fill(0, 0, c.width, c.height, c.populate);
  },

  /*
   * The function used to fill (populate) the grid when
   * constructed.
   */
  'prePopulate': function(t, x, y) {
    return this.defaults.fill;
  },

  /*
   * Get the tile at (x, y)
   */
  'get': function(x, y) {
    if (this.isOutOfBounds(x, y)) return this.tile.ob;
    return this._tiles[x + y * this.width];
  },

  /*
   * Set the tile at (x, y)
   */
  'set': function(x, y, v) {
    if (this.isOutOfBounds(x, y)) return false;
    if (this.validateTile(v)) {
      this._tiles[x + y * this.width] = v;
      return true;
    }
    return false;
  },

  /*
   * Check if tile will work with this grid
   * This is called before any set() and should
   * be overridden.
   */
  'validateTile': function(tile) {
    return true;
  },

  /*
   * Check if given (x, y) coordinates are "out of bounds"
   */
  'isOutOfBounds': function(x, y) {
    return x < 0 || x >= this.width || y < 0 || y >= this.height;
  },

  /*
   * Returns all neighboring tiles, numbered 0 through 7:
   *
   *  00 | 01 | 02
   *  03 | -- | 04
   *  05 | 06 | 07
   *
   * this is the same as calling grid.getRectNeighbors(x, y, 1, 1);
   */
  'neighbors': function(x, y) {
    var neighbors = [];

    neighbors[0] = this.get(x - 1, y - 1);
    neighbors[1] = this.get(x, y - 1);
    neighbors[2] = this.get(x + 1, y - 1);

    neighbors[3] = this.get(x - 1, y);
    neighbors[4] = this.get(x + 1, y);

    neighbors[5] = this.get(x - 1, y + 1);
    neighbors[6] = this.get(x, y + 1);
    neighbors[7] = this.get(x + 1, y + 1);

    return neighbors;
  },

  /*
   * Get all tiles within a (w by h) rectangle at (x, y)
   * returns a 2d array of composited tiles
   */
   'getInRect': function(x, y, w, h) {
     var tiles = [];
     for (var i = 0; i < h; i++) {
       tiles[i] = [];
       for (var j = 0; j < w; j++) {
         tiles[i][j] = this.get(x + j, y + i);
       }
     }
     return tiles;
   },

   /*
    *  Retrieve all tiles immediately surrounding the given rectangle
    */
   'getRectNeighbors': function(x, y, w, h) {
     var tiles = [];

     for (var i = -1; i < (h + 1); i++) {
       tiles[y + i] = [];
       tiles[y + i][x - 1] = this.get(x - 1, y + i);
       tiles[y + i][x + w] = this.get(x + w, y + i);
     }

     for (var i = 0; i < w; i++) {
       tiles[y - 1][x + i] = this.get(x + i, y - 1);
       tiles[y + h][x + i] = this.get(x + i, y + h);
     }

     return tiles;
   },

   /*
    * Transform the tile at given (x, y) by function fn.
    * The first argument of fn will be the tile,
    * second and third will be x and y of the tile (respectively)
    */
   'transform': function(x, y, fn) {
     return this.set(x, y, fn(this.get(x, y), x, y));
   },

   /*
    * Transform all tiles within given rectangle
    * by fn. The first argument of fn will be the tile,
    * second and third will be x and y of the tile (respectively)
    */
  'fill': function(x, y, w, h, fn) {
    for (var i = 0; i < h; i++) {
      for (var j = 0; j < w; j++) {
        this.transform(x + j, y + i, fn);
      }
    }
  },
});