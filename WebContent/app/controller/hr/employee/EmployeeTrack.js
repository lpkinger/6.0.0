Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.EmployeeTrack', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','core.tree.HrOrgTree'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({
	    		   'orgtreepanel':{
	    			   itemmousedown: function(selModel, record){
	    				   me.loadChild(selModel, record);
	    			   },
	    			   itemclick: function(selModel, record){
	    				   me.loadChild(selModel, record);
	    			   }
	    		   },
	    		   'panel[id=GMap]':{
	    			   afterrender:function(c){
	    				   map = new BMap.Map("GMap"); // 创建Map实例
	    				   map.centerAndZoom("深圳", 13); // 初始化地图,设置中心点坐标和地图级别
	    				   window.map = map;//将map变量存储在全局
	    				   map.addControl(new BMap.NavigationControl());
	    				   map.addControl(new BMap.ScaleControl());
	    				   map.addControl(new BMap.MapTypeControl());
	    				   map.enableScrollWheelZoom(); // 启用滚轮放大缩小
	    				   /*  var ctrl = new BMapLib.TrafficControl({
	    			                // 是否显示路况提示面板
	    			            });
	    			        map.addControl(ctrl);
	    			        ctrl.setAnchor(BMAP_ANCHOR_BOTTOM_RIGHT);*/

	    				   var data_info = [[116.417854,39.921988,"地址：北京市东城区王府井大街88号乐天银泰百货八层"],
	    				                    [116.406605,39.921585,"地址：北京市东城区东华门大街"],
	    				                    [116.412222,39.912345,"地址：北京市东城区正义路甲5号"]
	    				   ];
	    				   var opts = {
	    						   width : 250,     // 信息窗口宽度
	    						   height: 80,     // 信息窗口高度
	    						   title : "提示信息" , // 信息窗口标题
	    						   enableMessage:true//设置允许信息窗发送短息
	    				   };
	    				 /*  var pointTemp1 = new BMap.Point('113.887491','22.566747');
	    				   var pointTemp2 = new BMap.Point('113.909841','22.538331');
	    				   var pointTemp3 = new BMap.Point('113.959356','22.586436');
	    				   var markerTemp = new BMap.Marker(pointTemp2);
	    				   map.addOverlay(markerTemp);
	    				   markerTemp.setAnimation(BMAP_ANIMATION_BOUNCE);
	    				   var polylineTemp = new BMap.Polyline([pointTemp1,pointTemp2,pointTemp3], {
	    					   strokeColor : "red",
	    					   strokeWeight : 3,
	    					   strokeOpacity : 0.5
	    				   });
	    				   map.addOverlay(polylineTemp);*/
	    				   
	    				   var data_info = [[113.887491,22.566747,"地址：宝体 </br> 时间：2015-03-09 10:10:10 "],
	    									 [113.909841,22.538331,"地址：鲤鱼门 </br> 时间：2015-03-09 14:10:10"],
	    									 [113.959356,22.586436,"地址：西丽 </br>时间：2015-03-09 16:10:10"]
	    									],points=new Array(),point=null;
	    				   for(var i=0;i<data_info.length;i++){
	    					    point=new BMap.Point(data_info[i][0],data_info[i][1]);
	    					    points.push(point);
	    						var marker = new BMap.Marker(point);  
	    						var content = data_info[i][2];
	    						map.addOverlay(marker);             
	    						me.addClickHandler(content,marker);
	    					}
	    					var curve = new BMapLib.CurveLine(points, {strokeColor:"blue", strokeWeight:5, strokeOpacity:0.5}); //创建弧线对象
	    					map.addOverlay(curve); //添加到地图中
	    					curve.enableEditing();
	    			   }
	    		   }
	    	   });
	       },
	       loadChild:function (selmodel,record){
	    	   var me=this,tree=Ext.getCmp('orgtreepanel');

	    	   if(record.get('leaf')){

	    	   }else {
	    		   if(record.isExpanded() && record.childNodes.length > 0){
	    			   record.collapse(true,true);
	    			   me.flag = true;
	    		   } else {
	    			   if(record.childNodes.length == 0){
	    				   tree.loadChild(record);
	    			   }else record.expand(false,true);
	    		   }
	    	   }
	       },
	       addClickHandler:function(content,marker){
	    	   var me=this;
	    	   marker.addEventListener("click",function(e){
	    		   me.openInfo(content,e)}
	    	   );
	       },
	       openInfo:function(content,e){
	    	   var p = e.target;
	    	   var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
	    	   var infoWindow = new BMap.InfoWindow(content,{
					width : 250,   
					height: 80,    
					title : "位置信息" , 
					enableMessage:true
				   });  
	    	   map.openInfoWindow(infoWindow,point); 
	       }

});