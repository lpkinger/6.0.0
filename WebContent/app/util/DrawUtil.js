Ext.define('erp.util.DrawUtil', {
	draw: function(type, args){
		var me = this;
		return Ext.create('Ext.draw.Component', {
			viewBox: false,
			items: [me[type].apply(me, args)]
		});
	},
	batchDraw: function(data){
		var me = this,
			items = new Array();
		Ext.each(data, function(d){
			items.push(me[d.type].apply(me, d.args));
		});
		return Ext.create('Ext.draw.Component', {
			width: 1500,
			height: 2000,
			viewBox: false,
			items: items
		});
	},
	image: function(x, y, w, h, src, idx, handler){
		return {
			type: 'image',
	        x: x || 0,
	        y: y || 0,
	        cursor: 'pointer',
	        width: w,
	        height: h,
	        idx: idx,
	        src: src,
	        listeners: handler
		};
	},
	text: function(x, y, val, color, font, rotate){
		return {
			type: 'text',
	        text: val,
	        fill: color || 'blue',
	        font: font || '14px Arial',
	        cursor: 'pointer',
	        rotate: rotate,
	        x: x || 0,
	        y: y || 0
		};
	},
	circle: function(x, y, r, color, text){
		var me = this,
			res = new Array();
		res.push({
			type: 'circle',                     
	        fill: color || '#79BB3F',
	        radius: r || 10,
	        x: x || 0,
	        y: y || 0
		});
		if (text) {
			res.push(me.text(x - text.length*14/2, y, text, null, null));
		}
		return res;
	},
	rect: function(x, y, w, h, color, text){
		var me = this,
			r = new Array();
		r.push({
			type: 'rect',
			width: w || 30,
			height: h || 30,
			fill: color || '#C6E2FF',
			x: x || 0,
			y: y || 0,
			zIndex: -3
		});
		if (text) {
			r.push(me.text(x + (w - text.length*14)/2, y + h/2, text, null, null));
		}
		return r;
	},
	path: function(p, color, stroke){
		return {
			type: "path",
	        path: p, 
	        "stroke-width": "1",
	        stroke: stroke || "#CD950C",
	        fill: color || "gray"
		};
	},
	dotted: function(x, y, len, color, stroke){
		var p = 'M' + x + ' ' + y + ' ';
		for(var i = 0;i < len/3;i++) {
			x += 1;
			p += 'L' + x + ' ' + y + ' ';
			x += 3;
			p += 'M' + x + ' ' + y + ' ';
		}
		p += 'Z';
		return this.path(p, color, stroke);
	},
	line: function(x, y, len, angle, color, stroke, text, textAlign){//斜线
		if (!Ext.isNumber(angle)) {
			switch (angle) {
			case 'r' : {
				angle = 0;break;
			}
			case 'l' : {
				angle = 180;break;
			}
			case 't' : {
				angle = 90;break;
			}
			case 'b' : {
				angle = -90;break;
			}
			}
		}
		var me = this,
			t = me.getPointer(x, y, len, angle),
			a = t.x,
			b = t.y,
			p = 'M' + x + ' ' + y + ' L' + a + ' ' + b + ' Z';
		var r = new Array();
		r.push(me.path(p, color, stroke));
		if (text) {
			var l = 0,
				sl = me.lengthOf(text);
			if('center' == textAlign) {
				l = (len - sl)/2;
			} else if('right' == textAlign) {
				l = len;
			}
			if(sl > len) {
				t = me.getPointer(x, y + 10, l, angle);
				r.push(me.text(t.x, t.y, text.substring(0, text.length/2), color, null, {degrees: 360 - angle}));
				t = me.getPointer(x, y - 10, l, angle);
				r.push(me.text(t.x, t.y, text.substr(text.length/2 + 1), color, null, {degrees: 360 - angle}));
			} else {
				t = me.getPointer(x, y + 10, l, angle);
				r.push(me.text(t.x, t.y, text, color, null, {degrees: 360 - angle}));
			}
		}
		return r;
	},
	arrow: function(x, y, len, angle, color, stroke, text, textAlign){
		if (!Ext.isNumber(angle)) {
			switch (angle) {
			case 'r' : {
				angle = 0;break;
			}
			case 'l' : {
				angle = 180;break;
			}
			case 't' : {
				angle = 90;break;
			}
			case 'b' : {
				angle = -90;break;
			}
			}
		}
		var angle2 = angle - 180/len, angle3 = angle + 180/len;
		if (angle > 178) {
			angle3 -= 360;
		}
		if (angle < -178) {
			angle2 += 360;
		}
		var me = this,
			t1 = me.getPointer(x, y, len, angle),
			t2 = me.getPointer(x, y, len - 3, angle2),
			t3 = me.getPointer(x, y, len - 3, angle3),
			p = 'M' + x + ' ' + y + ' L' + t1.x + ' ' + t1.y + 
				' M' + t1.x + ' ' + t1.y + ' L' + t2.x + ' ' + t2.y +
				' M' + t2.x + ' ' + t2.y + ' L' + t3.x + ' ' + t3.y + 
				' M' + t3.x + ' ' + t3.y + ' L' + t1.x + ' ' + t1.y + ' Z';
		var r = new Array();
		r.push(me.path(p, color, stroke));
		if (text) {
			var l = 0,
				sl = me.lengthOf(text);
			if('center' == textAlign) {
				l = (len - sl)/2;
			} else if('right' == textAlign) {
				l = len - sl;
			}
			if(sl > len) {
				t = me.getPointer(x, y + 10, l, angle);
				var idx = me.indexOf(text, len);
				r.push(me.text(t.x, t.y, text.substring(0, idx), color, null, {degrees: 360 - angle}));
				t = me.getPointer(x, y + 25, l, angle);
				r.push(me.text(t.x, t.y, text.substr(idx), color, null, {degrees: 360 - angle}));
			} else {
				t = me.getPointer(x, y + 10, l, angle);
				r.push(me.text(t.x, t.y, text, color, null, {degrees: 360 - angle}));
			}
		}
		return r;
	},
	getPointer: function(x, y, len, angle) {
		var a = x,b = y, xP = 0,yP = 0;
		if (angle >= 0 && angle < 90) {
			yP = Math.abs(len * Math.sin(angle*Math.PI/180));
			xP = Math.abs(len * Math.cos(angle*Math.PI/180));
			a = x + xP, b = y - yP;
		} else if (angle >= 90 && angle <= 180) {
			angle = 180 - angle;
			yP = Math.abs(len * Math.sin(angle*Math.PI/180));
			xP = Math.abs(len * Math.cos(angle*Math.PI/180));
			a = x - xP, b = y - yP;
		} else if (angle >= -90 && angle < 0) {
			yP = Math.abs(len * Math.sin(-angle*Math.PI/180));
			xP = Math.abs(len * Math.cos(-angle*Math.PI/180));
			a = x + xP, b = y + yP;
		} else if (angle >= -180 && angle < -90) {
			angle = 180 + angle;
			yP = Math.abs(len * Math.sin(angle*Math.PI/180));
			xP = Math.abs(len * Math.cos(angle*Math.PI/180));
			a = x - xP, b = y + yP;
		}
		return {x: a, y: b};
	},
	lengthOf: function(str){ 
		var byteLen = 0; 
		if(str){ 
			for(var i=0,len = str.length; i<len; i++){ 
				if(str.charCodeAt(i) > 255){ 
					byteLen += 15; 
				} else{ 
					byteLen += 8; 
				} 
			} 
			return byteLen; 
		} else{ 
			return 0; 
		} 
	},
	indexOf: function(str, idx){ 
		var byteLen = 0; 
		if(str){ 
			for(var i=0,len = str.length; i<len; i++){ 
				if(str.charCodeAt(i) > 255){ 
					byteLen += 15; 
				} else{ 
					byteLen += 8; 
				}
				if(byteLen >= idx) {
					return i;
				}
			} 
			return str.length - 1;
		} else{ 
			return 0; 
		} 
	}
});