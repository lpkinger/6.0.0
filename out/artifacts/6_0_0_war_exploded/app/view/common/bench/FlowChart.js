Ext.define('erp.view.common.bench.FlowChart', {
	extend: 'Ext.draw.Component',
	alias: 'widget.flowChart',
	height: '100%',
	width: '100%',
	viewBox: false,
	maxSize: {width: 0, height: 0},
	initComponent: function() {
		var me = this;
		me.callParent(arguments);
	},
	listeners: {
		beforeAdd: function(draw, sprite) {
		},
		afterAdd: function(draw, sprite) {
			draw.el.dom.addEventListener("click", function (e) {
				var t = new Date();
				if(t - draw.clickTime > 500) {
					var ele = document.getElementsByClassName('x-flowchart-container')[0];
					if(ele){
						ele.parentNode.removeChild(ele);
					}
				}
			});
			draw.doLayout();
		},
		resize: function(draw,width,height) {
			draw.doLayout();
		},
		beforedestroy: function() {
			var ele = document.getElementsByClassName('x-flowchart-container')[0];
			if(ele){
				ele.parentNode.removeChild(ele);
			}
		}
	},
	add: function(sprite) {
		var me = this;
		me.fireEvent('beforeAdd', me, sprite);
		
		if(sprite.length == 0) {
			me.trueAdd([{
		    	type: 'text',
		    	text: '未设置流程图...',
		    	font: '20px Arial',
		    	fill: 'red',
		    	x: 0,
		    	y: 20
		   	}]);
			me.trueAdd();
		   	return false;
		}
		Ext.Array.each(sprite, function(s) {
			me.trueAdd(s);
		});
		
		me.fireEvent('afterAdd', me, sprite);
		return true;
	},
	trueAdd: function(s) {
		var me = this;
		var sprite = [];
		
		if(s.type == 'node' || s.type == 'node1') {
			var ptype = s.type,
				items = s.items,
				x = s.x,
				y = s.y,
				width = s.width || 100,
				height = s.height || 40,
				text = me.wrapText(s.text,width),
				fontSize = s.fontSize || (text.indexOf('\n') != -1 ? 12 : 14),
				bgColor = s.bgColor,
				color = s.color;
			if(me.isArray(items)){
				text = text + ' >>';
			}
			
			sprite.push({
				ptype: ptype,
				items: items,
		        type: 'rect',
		        fill: bgColor,
		        width: width,
		        height: height,
		        x: x,
		        y: y,
		        radius: 5
		   	}, {
		   		ptype: ptype,
		   		items: items,
		    	type: 'text',
		    	text: text,
		    	font: fontSize + 'px Arial',
		    	fill: color,
		    	x: x + width/2,
		    	y: y + height/2 - (text.indexOf('\n') != -1 ? 8 : 0),
		    	'text-anchor': 'middle'
		   	});
		}else if(s.type == 'node2') {
			var ptype = s.type,
				items = s.items,
				x = s.x,
				y = s.y,
				width = s.width || 100,
				height = s.height || 40,
				text = me.wrapText(s.text,width),
				fontSize = s.fontSize || (text.indexOf('\n') != -1 ? 12 : 14),
				textSize = me.getTextSize(text, fontSize),
				bgColor = s.bgColor || 'black',
				color = s.color || 'black',
				lineGap = 15;
				
			if(me.isArray(items)){
				text = text + ' >>';
			}
			
			sprite.push({
		   		ptype: ptype,
				type: 'path',
				path: 'M'+(x)+' '+(y)+' '+' L'+(x+width)+' '+y+' L'+(x+width)+' '+(y+height)+
					' L'+(x)+' '+(y+height)+' L'+(x)+' '+y+
					'M'+(x+lineGap)+' '+(y)+' L'+(x+lineGap)+' '+(y+height)+
					' M'+(x+width-lineGap)+' '+(y)+' L'+(x+width-lineGap)+' '+(y+height),
				stroke: bgColor,
		        'stroke-width': 1
		   	}, {
		   		ptype: ptype,
		   		items: items,
		    	type: 'text',
		    	text: text,
		    	font: fontSize + 'px Arial',
		    	fill: color,
		    	x: x + width/2,
		    	y: y + height/2 - (text.indexOf('\n') != -1 ? 8 : 0),
		    	'text-anchor': 'middle'
		   	});
		}else if(s.type == 'line') {
			var ptype = s.type;
				text = s.text || '',
				x1 = s.x,
				y1 = s.y,
				x2 = s.x2,
				y2 = s.y2,
				a1 = s.a1 || 0,
				a2 = s.a2 || 0,
				color = s.color || '#ababab',
				fontSize = s.fontSize || 12,
       	 		dot = s.dot;
				
			sprite.push({
				ptype: ptype,
				type: 'path',
				path: me.getLinePath(x1,y1,x2,y2,a1,a2),
				stroke: color,
		        'stroke-width': 2,
		        'stroke-dasharray': dot
			}, {
				ptype: ptype,
          		type: 'text',
          		text: text,
          		x: (x1 + x2)/2,
	         	y: (y1 + y2)/2,
	          	font: fontSize + 'px Arial',
	          	fill: color,
	          	'text-anchor': 'middle'
			});
		}else if(s.type == 'rect') {
	      	var ptype = s.type,
		        x = s.x,
		        y = s.y,
		        width = s.width,
		        height = s.height,
		        color = s.color || '#ababab',
		        bgColor = s.bgColor || 'transparent',
		        dot = s.dot || 0;
      
			sprite.push({
		        ptype: ptype,
		        type: 'rect',
				x: x,
				y: y,
				width: width,
				height: height,
				fill: bgColor,
				stroke: color,
				'stroke-width': 1,
				'stroke-dasharray': dot
  			});
    	}else if(s.type == 'text') {
        	var ptype = s.type,
		        text = s.text,
		        fontSize = s.fontSize,
		        textSize = me.getTextSize(text, fontSize),
		        color = s.color || 'black';
		        x = s.x - textSize.width/2,
		        y = s.y;
		        
      
      		sprite.push({
        		ptype: ptype,
          		type: 'text',
          		text: text,
          		x: x,
	         	y: y,
	          	font: fontSize + 'px Arial',
	          	fill: color,
	          	'text-anchor': 'middle'
        	});
		}else if(s.type == 'diamond') {
			var ptype = s.type,
				items = s.items,
				x = s.x,
				y = s.y,
				width = s.width || 100,
				height = s.height || 40,
				points = ''+x+','+(y+height/2)+' '+(x+width/2)+','+y+
					' '+(x+width)+','+(y+height/2)+' '+(x+width/2)+','+(y+height),
				text = me.wrapText(s.text,width),
				fontSize = s.fontSize || (text.indexOf('\n') != -1 ? 12 : 14),
				textSize = me.getTextSize(text, fontSize),
				bgColor = s.bgColor,
				color = s.color,
				textWidth = textSize.width,
				textHeight = textSize.height;
		
			sprite.push({
        		ptype: ptype,
          		type: 'polygon',
          		items: items,
          		points: points,
	          	fill: bgColor
        	},	{
        		ptype: ptype,
		   		items: items,
		    	type: 'text',
		    	text: text,
		    	font: fontSize + 'px Arial',
		    	fill: color,
		    	x: x + width/2,
		    	y: y + height/2 - (text.indexOf('\n') != -1 ? 8 : 0),
		    	'text-anchor': 'middle'
        	});
		}else if(s.type == 'area') {
			var ptype = s.type,
				points = '',
				color = s.color || 'black';
			
			if(s.points instanceof Array) {
				points =s.points.map(function(p) {
					return ' '+p[0]+','+p[1];
				});
			}
			
			sprite.push({
        		ptype: ptype,
          		type: 'polygon',
          		points: points,
	          	fill: color
        	});
			
		}else {
			sprite = s;
		}
		var ss = [];
		Ext.Array.each(sprite, function(s) {
			me.willBeforeAdd(s);
			var sp = me.surface.add(s);
			sp.show(true);
			ss.push(sp);
		});
		me.didAfterAdd(me, ss);
		return ss;
	},
	removeAll: function() {
		var me = this;
		var sf = me.surface;
		sf.removeAll();
	},
	willBeforeAdd: function(sprite) {
		var me = this;
		var draggable = sprite.draggable,
			listeners = sprite.listeners || {};
		
		if(draggable) {
			if(listeners.render) {
				listeners.render = function(sprite, event) {
					sprite.draggable = true;
					listeners.render(sprite, event);
				}
			}else {
				listeners.render = function(sprite) {
					sprite.draggable = true;
					sprite.initDraggable();
				}
			}
		}
		return sprite;
	},
	didAfterAdd: function(draw,sprite) {
		var me = this;
		
		Ext.Array.each(sprite, function(s) {
			// 对节点类型添加点击事件
			if(s.ptype == 'node') {
				var items = s.items;
				if(items) {
					s.el.dom.style.cursor = 'pointer';
					var tspan = s.el.dom.getElementsByTagName('tspan')[0];
					if(tspan)tspan.style.cursor = 'pointer';
					
					if(items instanceof Array) {
						s.el.dom.addEventListener("click", function (e) {
							me.clickTime = new Date();
							var ele = document.getElementsByClassName('x-flowchart-container')[0];
							if(ele){
								ele.parentNode.removeChild(ele);
							}
							me.showMenu(items, e);
						});
					}else {
						if(items.url) {
							/*if(s.type === 'text') {
								s.el.dom.style.textDecorationLine = 'underline';
								s.el.dom.style.fill = 'blue';
							}*/
							s.el.dom.addEventListener("click", function () {
								openUrl2(items.url,items.title);
							});
						}
					}
				}
			}else if(s.type == 'polygon') { // 针对图形做处理
				var p = s.el.dom;
				if(p) {
					p.setAttribute('points', s.points);
				}
			}
			// 记录最大高度
			if(s.el) {
				var layout = s.el.dom.getBBox();
				me.maxSize = {
					width: me.maxSize.width < (layout.x + layout.width) ? (layout.x + layout.width) : me.maxSize.width,
					height: me.maxSize.height < (layout.y + layout.height) ? (layout.y + layout.height) : me.maxSize.height
				}
			}
		});
	},
	showMenu: function(items, e) {
		var me = this;
		var menu = me.createMenu(items);
		var length = menu.items.items[0].items.items[0].items.length * 27;
		var windowHeight = document.body.clientHeight,
			windowWidth = document.body.clientWidth;
		if(e.y+length > windowHeight){
			var y = windowHeight - length - 20;
			if(e.x+menu.width > windowWidth){
				var x = windowWidth - menu.width - 70;
				menu.showAt(x,y);
			}else{
				menu.showAt([e.x,y]);
			}
		}else{
			if(e.x+menu.width > windowWidth){
				var x = windowWidth - menu.width - 70;
				menu.showAt(x,e.y);
			}else{
				menu.showAt([e.x,e.y]);
			}
		}
		var x = 0;
		Ext.Array.each(menu.items.items,function(item,i){
			x +=item.getWidth();
		});
		menu.setWidth(x);
		document.getElementsByClassName('x-flowchart-container')[0].style.backgroundColor = 'white';
		var menuArray = menu.items.items;
		var lastMenu = menuArray[menuArray.length-1];
		lastMenu.setHeight(lastMenu.getHeight()-4);
		lastMenu.setWidth(lastMenu.getWidth()-4);
		lastMenu.el.dom.getElementsByClassName('x-menu-body')[0].style.padding = 0;
	},
	createMenu: function(items) {
		var cols = [];
		var menu = new Ext.menu.Menu({floating:false});
		var length = Math.ceil(items.length / 8); 
		
		Ext.Array.each(items, function(item,index) {
			if(index != 0 && index % 8 == 0){
				cols.push({
					items: menu
				});
				menu = new Ext.menu.Menu({cls: 'x-flowchart-menu', floating:false});
			}
			menu.add({
				text: item.text,
				url: item.url,
				style: 'backgroundColor:white;border:none;',
				handler: function() {
					//关闭菜单
					var ele = document.getElementsByClassName('x-flowchart-container')[0];
					ele.parentNode.removeChild(ele);
					//打开URL
					openUrl2(item.url,item.title);
				}
			});
			
		});
			cols.push({
				items: menu
			});
		var columnMenu = new Ext.container.Container({
			cls: 'x-flowchart-container',
			layout: 'column',
			shadow: false,
			floating: true,
			items: cols
		});
		if(cols.length == 1){
			columnMenu.setWidth(178);
		}else{
			columnMenu.setWidth(cols.length * 136);
		}
		return columnMenu;
	},
	getTextSize: function(text, fontSize) {
		var me = this;
		var span = document.createElement("pre");
	    var result = {};
	    result.width = span.offsetWidth;
	    result.height = span.offsetWidth; 
	    span.style.visibility = "hidden";
	    span.style.wordWrap = 'break-word';
	    span.style.position = 'absolute';
	    span.style.fontSize = fontSize + 'px';
	    document.body.appendChild(span);
	    if (typeof span.textContent != "undefined")
	        span.textContent = text;
	    else span.innerText = text;
	    result.width = span.getBoundingClientRect().width;
	    result.height = span.getBoundingClientRect().height;
	    span.parentNode.removeChild(span);
	    return result;
	},
	/**
	 * 为文字添加换行符(只支持换一行)
	 */
	wrapText: function(text,rectWidth) {
		var text = text.replace(/\s/g,'') || '';
		var w_ = ~~(rectWidth/20);
		if(text.length > w_) {
			if(text.length > 2*w_) {
				text = text.substring(0,w_) + '\n' + text.substring(w_,2*w_-1) + '...'; 
			}else {
				text = text.substring(0,w_) + '\n' + text.substring(w_);
			}
		}
		return text;
	},
	getLinePath: function(x1,y1,x2,y2,arrowLeft, arrowRight) {
		var path,
      		slopy,cosy,siny,
      		Par=10.0,
      		x3,y3,
      		slopy=Math.atan2((y1-y2),(x1-x2)),
      		cosy=Math.cos(slopy),
      		siny=Math.sin(slopy);
		
		path="M"+x1+","+y1+" L"+x2+","+y2;  
           
		x3=(Number(x1)+Number(x2))/2; // 中点x 
		y3=(Number(y1)+Number(y2))/2; // 中点y
		
		function drawArrow(x,y,dir) {
			var path = '';
			if(!Number.isNaN(dir) && dir != 0) {
				path +=" M"+x+","+y;  
				path +=" L"+(Number(x)+dir*Number(Par*cosy-(Par/2.0*siny)))+","+(Number(y)+dir*Number(Par*siny+(Par/2.0*cosy)));  
				path +=" M"+(Number(x)+dir*Number(Par*cosy+Par/2.0*siny)+","+ (Number(y)-dir*Number(Par/2.0*cosy-Par*siny)));  
				path +=" L"+x+","+y;
			}
			return path;
		}
		
		path += drawArrow(x1,y1,Number(arrowLeft));
		path += drawArrow(x2,y2,Number(arrowRight));
		
		return path;
	},
	/**
	 * 根据元素位置设置svg容器高宽以显示滚动条
	 */
	doLayout: function() {
		var draw = this,
			width = draw.el.dom.offsetWidth,
			height = draw.el.dom.offsetHeight,
			svgEl = draw.el.dom.getElementsByTagName('svg')[0];
		if (draw.maxSize.width == draw.maxSize.height && draw.maxSize.width == 0) {
			return;
		}
		if(draw.maxSize.width < width)
			draw.el.dom.style.overflowX = 'hidden';
		
		if(draw.maxSize.width > width){
			draw.el.dom.style.overflowX = 'scroll';
		}else{
			var left = width - draw.maxSize.width;
			svgEl.style.marginLeft = left/2 + 'px';
		}
		if(draw.maxSize.height > height) {
			draw.el.dom.style.overflowY = 'scroll';
		}
		svgEl.style.width = draw.maxSize.width+10 + 'px';
		svgEl.style.height = draw.maxSize.height+10 + 'px';
	},
	/**
	 * 判断是否是数组
	 */
	isArray: function(o) {
		return Object.prototype.toString.call(o)=='[object Array]';
	}
});