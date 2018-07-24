Ext.define('erp.view.hr.employee.HrOrgMap', {
	extend: 'Ext.draw.Component',
	alias: 'widget.basedraw',
	viewBox: false,
	count: 0,
	initComponent: function() {
		var me = this;
		me.horizontalDeep = 4; // 横向展示深度
		me.nodeSize = {width: 90, height:40}; // 节点大小
		me.nodeGap = 10; // 节点间距
		me.contentSize = {width: 0, height: 0};
		me.callParent(arguments);
	},
	listeners: {
		beforeAdd: function(draw, node) {
		},
		afterAdd: function(draw, node) {
			draw.doLayout();
		},
		resize: function(draw,width,height) {
			draw.doLayout();
		}
	},
	add: function(node) {
		var me = this;
		me.count = 0;
		me.fireEvent('beforeAdd', me, node);
		me.setDeepValue(node, 0, {level: 0, posX: 1, posY: 0});
		me.allWidth = node.deepX * (me.nodeSize.width + me.nodeGap); // 总宽度
		me.setNodeLayout(node, {x: me.nodeGap, y: 0, deepX: node.deepX, deepY: node.deepY + 1, lastX: node.deepX, lastY: node.deepY+1})
		me.trueAdd(node);
		me.fireEvent('afterAdd', me, node);
		return true;
	},
	/** 设置各个节点的横纵深度 **/
	setDeepValue: function(node, index, parent) {
		var me = this;me.count++;
		
		if(node.level > me.horizontalDeep) {
			node.deepX = 0.5;
			node.deepY = 1;
		}else {
			node.deepX = 1;
			node.deepY = 1;
		}
		
		node.level = parent.level + 1;
		node.maxDeepX = node.maxDeepY = node.sumDeepX = node.sumDeepY = 0;
		
		if(node.children instanceof Array && node.children.length > 0) {
			Ext.Array.each(node.children, function(s, i) {
				me.setDeepValue(s, i, node);
				node.sumDeepX += s.deepX;
				node.sumDeepY += s.deepY;
				node.maxDeepX = node.maxDeepX > s.deepX ? node.maxDeepX : s.deepX;
				node.maxDeepY = node.maxDeepY > s.deepY ? node.maxDeepY : s.deepY;
			});
		}
		if(node.level > me.horizontalDeep) {
			node.deepX = 0.5 + node.maxDeepX;
			node.deepY = 1 + node.sumDeepY;
		}else {
			if(!node.children || node.children.length == 0) {
				node.deepX = 1;
				node.deepY = 1;
			}else {
				if(node.level < me.horizontalDeep) {
					node.deepX = node.sumDeepX;
					node.deepY = 1 + node.maxDeepY;
				}else {
					node.deepX = 1 + node.maxDeepX;
					node.deepY = 1 + node.sumDeepY;
				}
			}
		}
		
		delete node.sumDeepX;
		delete node.maxDeepX;
		delete node.sumDeepY;
		delete node.maxDeepY;
	},
	/** 设置各个节点的大小位置 **/
	setNodeLayout: function(node, parent) {
		var me = this;
		
		node.lastX = node.deepX;
		node.lastY = node.deepY;
		
		if(node.level > me.horizontalDeep) {
			node.x = parent.x + me.nodeSize.width*0.5 + me.nodeGap;
			node.y = parent.y + me.nodeSize.height + (parent.deepY - parent.lastY) * (me.nodeGap + me.nodeSize.height) + me.nodeGap;
			parent.lastY -= node.deepY;
		}else {
			node.x = parent.x + (parent.deepX - parent.lastX) * (me.nodeGap + me.nodeSize.width);
			node.y = parent.y + me.nodeSize.height + me.nodeGap*2;
			parent.lastX -= node.deepX;
		}
		
		if(node.children instanceof Array && node.children.length > 0) {
			Ext.Array.each(node.children, function(s) {
				me.setNodeLayout(s, node);
			});
			// 根据子节点位置调整父节点位置居中
			if(node.level < me.horizontalDeep) {
				node.x = (node.children[0].x + node.children[node.children.length-1].x)/2;
			}
		}
		delete node.lastX;
		delete node.lastY;
		me.setNodePoint(node);
	},
	trueAdd: function(node, parent) {
		
		var me = this;
		var nodeEls = [];
		var x = node.x,
			y = node.y,
			text = me.wrapText(node.text) || '',
			fontSize = (text.indexOf('\n') != -1 ? 12 : 14),
			textSize = me.getTextSize(text, fontSize),
			width = me.nodeSize.width,
			height = me.nodeSize.height,
			bgColor = node.bgColor || '#69bcec',
			color = node.color || 'white',
			textWidth = textSize.width,
			textHeight = textSize.height
			
		nodeEls.push({
	        type: 'rect',
	        fill: bgColor,
	        width: width,
	        height: height,
	        x: x,
	        y: y,
	        radius: 5
	   	}, {
	    	type: 'text',
	    	text: text,
	    	font: fontSize + 'px Arial',
	    	fill: color,
	    	x: x + width/2,
	    	y: y + height/2 - (text.indexOf('\n') != -1 ? 8 : 0),
	    	'text-anchor': 'middle'
	   	});
	   	if(parent) {
	   		nodeEls.push({
		   		type: 'path',
		   		path: me.getNodeLine(node, parent),
		   		stroke: '#69bcec',
			    'stroke-width': 2
		   	});
	   	}
		
		if(node.children instanceof Array && node.children.length > 0) {
		 	Ext.Array.each(node.children, function(s) {
		 		me.trueAdd(s, node);
		 	});
		}
		
		var ss = [];
		Ext.Array.each(nodeEls, function(s) {
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
		//重置滚动条位置
		me.el.dom.scrollTop = 0;
		me.el.dom.scrollLeft = 0;
		sf.removeAll();
		me.contentSize = {width: 0, height: 0};
		me.doLayout();
	},
	willBeforeAdd: function(node) {
		var me = this;
		var draggable = node.draggable,
			listeners = node.listeners || {};
		
		if(draggable) {
			if(listeners.render) {
				listeners.render = function(node, event) {
					node.draggable = true;
					listeners.render(node, event);
				}
			}else {
				listeners.render = function(node) {
					node.draggable = true;
					node.initDraggable();
				}
			}
		}
		return node;
	},
	didAfterAdd: function(draw,node) {
		var me = this;
		Ext.Array.each(node, function(s) {
			var layout = s.el.dom.getBoundingClientRect();
			me.contentSize = {
				width: me.contentSize.width > (layout.left + layout.width) ? me.contentSize.width : (layout.left + layout.width),
				height: me.contentSize.height > (layout.top + layout.height) ? me.contentSize.height : (layout.top + layout.height)
			}
		});
	},
	/** 设置节点连线的起止点位置 **/
	setNodePoint: function(node) {
		var me = this;
		if(node.level <= me.horizontalDeep) {
			node.inPoint = {
				x: node.x + me.nodeSize.width/2,
				y: node.y
			};
			node.outPoint = {
				x: node.x + me.nodeSize.width/2,
				y: node.y + me.nodeSize.height
			}
		}else {
			node.inPoint = {
				x: node.x,
				y: node.y + me.nodeSize.height/2
			},
			node.outPoint = {
				x: node.x + me.nodeSize.width/2,
				y: node.y + me.nodeSize.height
			}
		}
	},
	showMenu: function(items, e) {
		var me = this;
		var menu = me.createMenu(items);
		menu.showAt([e.x, e.y])
	},
	createMenu: function(items) {
		var menu = new Ext.menu.Menu();
		Ext.Array.each(items, function(item) {
			menu.add({
				text: item.text,
				url: item.url,
				style: 'backgroundColor:white;border:none;',
				handler: function() {
					/*openUrl2(item.url,item.title);*/
				}
			});
		});
		return menu;
	},
	/**
	 * 为文字添加换行符(只支持换一行)
	 */
	wrapText: function(text) {
		var w = text.replace(/\s/g,'') || '';
		if(w.length > 5) {
			if(w.length > 10) {
				w = w.substring(0,5) + '\n' + w.substring(5,9) + '...'; 
			}else {
				w = w.substring(0,5) + '\n' + w.substring(5)
			}
		}
		return w;
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
	 * 根据两个节点级别设置连线
	 **/
	getNodeLine: function(node, parent) {
		var me = this,
			px = parent.outPoint.x,
			py = parent.outPoint.y,
			sx = node.inPoint.x,
			sy = node.inPoint.y,
			line = '';
		
		if(node.level <= me.horizontalDeep) {
			line += ' M'+px+','+py+' L'+px+','+(py+me.nodeGap)+' L'+sx+','+(sy-me.nodeGap)+' L'+sx+','+sy;
		}else {
			line += ' M'+px+','+py+' L'+px+','+sy+' L'+sx+','+sy;
		}
		return line;
	},
	/**
	 * 根据元素位置设置svg容器高宽以显示滚动条
	 */
	doLayout: function() {
		var draw = this,
			width = draw.el.dom.offsetWidth,
			height = draw.el.dom.offsetHeight,
			svgEl = draw.el.dom.getElementsByTagName('svg')[0];
		
		if(draw.contentSize.width > width) {
			draw.el.dom.style.overflowX = 'scroll';
		}else {
			draw.el.dom.style.overflowX = 'hidden';
		}
		if(draw.contentSize.height > height) {
			draw.el.dom.style.overflowY = 'scroll';
		}else {
			draw.el.dom.style.overflowY = 'hidden';
		}
		svgEl.style.width = draw.contentSize.width+10 + 'px';
		svgEl.style.height = draw.contentSize.height+10 + 'px';
	}
});