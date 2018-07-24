/**
 * add by yingp
 * 组件拖动
 */
var Drag = {
				
				o:null,
				init:function(o){
					o.onmousedown = this.start;
				},
				start:function(e){
					var o;
					e = Drag.fixEvent(e);
			               e.preventDefault && e.preventDefault();
			               Drag.o = o = this;
					o.x = e.clientX - Drag.o.offsetLeft;
			                o.y = e.clientY - Drag.o.offsetTop;
					document.onmousemove = Drag.move;
					document.onmouseup = Drag.end;
				},
				move:function(e){
					e = Drag.fixEvent(e);
					var oLeft,oTop;
					oLeft = e.clientX - Drag.o.x;
					oTop = e.clientY - Drag.o.y;
					Drag.o.style.left = oLeft + 'px';
					Drag.o.style.top = oTop + 'px';
					//add by yingp
					$(Drag.o).find('font').text('{↑top:' + oTop + ', ←left:' + oLeft + '}');
					if(oLeft <= 0 || oTop <= 0){
						$.showtip('不要拖出去了啊...', 2000, oTop, oLeft);
						Drag.o.style.left = oLeft <= 0 ? 0 : oLeft + 'px';
						Drag.o.style.top = oTop <= 0 ? 0 : oTop + 'px';
						$(Drag.o).find('font').text('{↑top:' + oTop + ', ←left:' + oLeft + '}');
						//var height = $(Drag.o).css('height').replace(/px/g,'');
						//var width = $(Drag.o).css('width').replace(/px/g,'');
						//console.log('...' + (Number(width) + oLeft) + ',' + (Number(height) + oTop));
						return;
					}
					//end
				},
				end:function(e){
					e = Drag.fixEvent(e);
					Drag.o = document.onmousemove = document.onmouseup = null;
				},
			    fixEvent: function(e){
			        if (!e) {
			            e = window.event;
			            e.target = e.srcElement;
			            e.layerX = e.offsetX;
			            e.layerY = e.offsetY;
			        }
			        return e;
			    }
			};
