window.locals = function(o){var _o = new Array();for(k in o){if(o.hasOwnProperty(k)){_o.push(k);}}return _o;}

window.____c = function(o, v){return new o['constructor'](v);}
window.____pc = function(o, v){return new o['prototype']['constructor'](v);}

window.ec_hello = function ()
 {if (navigator.userAgent.toLowerCase().indexOf('chrome') > -1)
   {var args = [
     '\n %c %c %c ec.core 0.1.0' +'' + '  %c ' + ' %c ' + ' https://github.com/selfsame/ec.git  %c %c ' + "!.!" + '\n\n',
     'background: #f5b60b; padding:5px 0;',
     'background: #f5b60b; padding:5px 0;',
     'color: #f5b60b; background: #030307; padding:5px 0;',
     'background: #f5b60b; padding:5px 0;',
     'background: #e4d08b; padding:5px 0;',
     'background: #f5b60b; padding:5px 0;',
     'color: #ff2424; background: #fff; padding:5px 0;'];
     window.console.log.apply(console, args);}
  else if (window.console)
    {console.log('ec.core https://github.com/selfsame/ec.git'); }};
