Ext.define('erp.view.crm.chance.HopperDraw',{ 
	extend: 'Ext.draw.Component', 
	alias: 'widget.hopperdraw',
	width: 400,
	height: 400,
	cls: 'cursor-dragme',
	viewBox: true,
	initComponent : function(){
		var me=this;
		Ext.applyIf(me,{
			items:me.getItems()
		});
		me.callParent(arguments); 
		this.addEvents(
	            'mousedown',
	            'mouseup',
	            'mousemove',
	            'mouseenter',
	            'mouseleave',
	            'click'
	        );
	},
	getItems:function(){
		var hoppers=this.getSaleHopper();
		return this.getPaths(hoppers);
	},
	getSaleHopper:function(con){
		var o,param=new Object();
		if(con)param.condition=con;
		Ext.Ajax.request({
			url : basePath + 'crm/business/getHopperByCondition.action',
			params: param,
			async: false,
			method : 'get',
			callback : function(options,success,response){
                var res=new Ext.decode(response.responseText);
                o=res.counts;
			}
		});
		return o;
	},
	getPaths:function(hoppers){
		var allcount=0,s=0.556,h=200,maxWidth=300,minWidth=100,tW=110,itemH=h/hoppers.length;
		var items=new Array();
		Ext.Array.each(hoppers,function(a){
			allcount+=a.count;
		});
		var items=new Array(),point1,point2,point3,point4,linepoint1,linepoint2,linepoint3,linepoint4,linepoint5;
		var start={
				x:12,
				y:0
		};
		Ext.Array.each(hoppers,function(item,index){
			var _LC=0,_LY=0,_LX=0,_CY=0,_CX=0,count=item.count;
			if(index>0){
				for(var k=0;k<index;k++){
					_LC+=hoppers[k].count;
				}
			}
			_LY=h*(_LC/allcount);
			_LX=0.5*_LY;
			_CY=h*((_LC+count)/allcount);
			_CX=0.5*_CY;
			point1={
					x:start.x+_LX,
					y:start.y+_LY
			};
			point2={
					x:start.x+maxWidth-_LX,
					y:start.y+_LY
			};
			point3={
					x:start.x+maxWidth-_CX,
					y:start.y+_CY
			};
			point4={
					x:start.x+_CX,
					y:start.y+_CY
			};
			linepoint1={
					x:start.x+maxWidth+20,
					y:start.y+itemH*index
			};
			linepoint2={
					x:start.x+maxWidth+15,
					y:start.y+itemH*index	
			};
	        linepoint3={
	        		x:start.x+maxWidth+5,
	        		y:start.y+_LY+h*count/(2*allcount)
	        };
	        linepoint4={
	        		x:start.x+maxWidth,
	        		y:start.y+_LY+h*count/(2*allcount)
	        };
	        linepoint5={
	        		x:start.x+maxWidth-(_LY+h*count/(2*allcount))*0.5,
	        		y:start.y+_LY+h*count/(2*allcount)
	        };
			items.push({
				type:'path',
				path:"M "+point1.x+" "+point1.y+" L "+point2.x+" "+point2.y+" "+point3.x+" "+point3.y+" "+point4.x+" "+point4.y+" Z",
				fill:'#'+item.color,
				stroke:"#FFFFFF",
				"stroke-width":"1"
			});
			items.push({
				type: "text",
				text: item.name+"("+item.count+")",
				x:linepoint1.x,
				y:linepoint1.y,
				fill: "black",
				label:item.name,
				font: "12px Lucida Grande,Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif;",
				listeners :{
					mousedown:function(e){
					  var grid=Ext.getCmp('chancegrid');
					  grid.GridUtil.loadNewStore(grid,{
						  caller:grid.caller,
						  condition:"bc_currentprocess='"+e.label+"'"
					  });
					}
				}
			});
			items.push({
				type: "path",
				path: "M "+linepoint1.x+" "+linepoint1.y+" C "+linepoint2.x+" "+linepoint2.y+" "+linepoint3.x+" "+linepoint3.y+" "+linepoint4.x+" "+linepoint4.y+" L "+" "+linepoint5.x+" "+linepoint5.y,
				"stroke-width":"1",
				visibility:"visible",
				stroke:"gray",
				fill:"none"
			});
	});
	return items;
 }
});