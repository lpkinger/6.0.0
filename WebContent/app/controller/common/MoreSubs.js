Ext.QuickTips.init();
Ext.define('erp.controller.common.MoreSubs', {
	extend : 'Ext.app.Controller',
	activeRefresh:true,
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['common.DeskTop.MoreSubs', 'common.datalist.GridPanel',
			'common.datalist.Toolbar', 'core.grid.TfColumn',
			'core.grid.YnColumn','core.grid.HeaderFilter','common.DeskTop.DeskTabPanel'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		this.control({
			'erpDatalistGridPanel' : {
				 itemclick: this.onGridItemClick
			}
		});
	},
	onGridItemClick: function(selModel, record){
		this.showWin(record.data.num_id_,record.data.instance_id_,record.data.id_,record.data.title_,'Subs');				
		var grid = Ext.getCmp("Subs");		
		if(grid){					
			grid.getColumnsAndStore();			
			}
		
	},
	showWin :function (numId,mainId,insId,title,id){ 
		var me=this;
		var url='common/charts/mobileCharts.action?numId='+numId+'&mainId='+mainId+'&insId='+insId+'&title='+title;
		if (Ext.getCmp('chwin')) {
			Ext.getCmp('chwin').setTitle(title);
			Ext.getCmp('chwin').insId=insId;
			Ext.getCmp('chwin').body.update('<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>');
			}
		else {
		var chwin = new Ext.window.Window({
		   id : 'chwin',
		   title: title,
		   height: "100%",
		   width: "80%",
		   insId:insId,	   
		   maximizable : true,
		   resizable:false,
		   modal:true,
		   buttonAlign : 'center',
		   layout : 'anchor',
		   listeners:{
			   afterrender: function(th) {
				   th.on('resize', function(){
					   var iframe = document.getElementById('iframech');
					   var src = iframe.src;
					   if(src.indexOf('time_')>=0){
						   iframe.src=src.substring(0,src.indexOf('time_')-1)+'&time_='+new Date().getTime();
					   }else{
						   iframe.src=iframe.src+'&time_='+new Date().getTime();
					   }
				   })
			   }
		   },
		   items: [{
			   tag : 'iframe',
			   frame : true,
			   anchor : '100% 100%',
			   layout : 'fit',
			   html : '<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>'
		   }],
		   buttons : [{
			   text : '上一条',		  
			   cls: 'x-btn-gray',
			   handler : function(btn){
				   me.prev(btn,id,btn.ownerCt.ownerCt.insId);
			   }
		   },{
			   text : '下一条',	
			   cls: 'x-btn-gray',
			   handler : function(btn){
				   me.next(btn,id,btn.ownerCt.ownerCt.insId);
			   }
		   },{
			   text : '关  闭',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler : function(){
				   Ext.getCmp('chwin').close();
				   var grid = Ext.getCmp("Subs");		
					if(grid){													
						grid.getColumnsAndStore();						
						}
			   }
		   }]
	   });
	   
	 chwin.on('close',function(btn){
			var grid = Ext.getCmp("Subs");		
			if(grid){												
				grid.getColumnsAndStore();
				}

		});
	   
		chwin.show();}},

		prev:function(btn,tabId,insId,index){
		//递归查找下一条，并取到数据
		var grid=Ext.getCmp(tabId);
		var record =index?grid.store.getAt(index):grid.store.findRecord('id_', insId, 0, false, false, true);
	    var fIndex=index||record.index;
	    if(fIndex-1 >=0){
	    	var d = grid.store.getAt(fIndex - 1);
	    	if(d){
	    		if(d.data['id_']==insId){       		
	    			this.prev(btn,tabId,insId,d.index);//过滤因合计数据重复显示的记录
	    			}
	    		else {this.showWin(d.data['num_id_'],d.data['instance_id_'],d.data['id_'],d.data['title_'],tabId);}
	    	}}
	    else alert('暂无上一条数据');//btn.setDisabled(true);
	},
		next:function(btn,tabId,insId,index){
		//递归查找下一条，并取到数据
		var grid=Ext.getCmp(tabId);
		var record =index?grid.store.getAt(index):grid.store.findRecord('id_', insId, 0, false, false, true);
	    var fIndex=index||record.index;
	    if(fIndex+1 < grid.store.data.items.length){
	    	var d = grid.store.getAt(fIndex + 1);
	    	if(d){
	    		if(d.data['id_']==insId){       		
	    			this.next(btn,tabId,insId,d.index);
	    			}
	    		else {this.showWin(d.data['num_id_'],d.data['instance_id_'],d.data['id_'],d.data['title_'],tabId);}
	    	}}
	    else alert('暂无下一条数据');//btn.setDisabled(true);
	},
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab) activeTab.fireEvent('activate',activeTab);
	}
});