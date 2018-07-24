Ext.QuickTips.init();
Ext.define('erp.controller.ma.SysCheckScan', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[ 'ma.SysCheckTreeGrid',
      		'core.form.Panel','ma.SysCheckFormula','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField','ma.SysCheckGrid',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.PrintA4','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit','ma.SysCheckWinGrid',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.grid.YnColumn','ma.SysCheckTree','ma.SysCheckForm','core.form.ConDateField'
  			],
    init:function(){
    	var me = this;
    	this.control({ 
           'erpSysCheckTreePanel':{
        	   itemmousedown: function(selModel, record){
   				if(record.get('leaf')){
   			       //人员点进去的  查看个人明细信息
   					var main = parent.Ext.getCmp("content-panel");
   					if(!main)
   						main = parent.parent.Ext.getCmp("content-panel");
   					if(main){
   						main.getActiveTab().setLoading(true);//loading...
   					}
			       Ext.getCmp('sysgrid').hide();
			       var grid=Ext.getCmp('gridpanel');
			       if(grid){
			    	   grid.show();
			    	   grid.store.clearFilter(true);
			    	   grid.store.filter("scd_emname", record.data.text);
			    	   me.setToolbarValue(grid);
			    	   main.getActiveTab().setLoading(false);
			       }else {
			    	   Ext.getCmp('mainplace').add({
				    	    anchor:'100% 93%',
							layout:'fit',
							autoScroll:true,
							id:'gridpanel',
							xtype : 'erpGridPanel2',
							caller : 'SysCheckData',
							condition:'1=1',
							listeners:{
								'afterrender':function(grid){
									grid.store.filter("scd_emname", record.data.text);
								}
							}
				       });
			    	   
			       }
			       
   				}else {
				   //组织点进去的  查看合计信息
			    	if(record.data.parentId=='root'){
			    		var sysgrid=Ext.getCmp('sysgrid');
			    		sysgrid.loadNewStore(sysgrid,{_noc:1,parentid:0});
			    		sysgrid.show();
			    		var grid= Ext.getCmp('gridpanel');
			    		if (grid) grid.hide();
			    	}else if(record.data.cls&&record.data.cls=='org'){
			    		//说明点开的还是
			    		var grid= Ext.getCmp('gridpanel');
			    		if (grid) grid.hide();
			    		var sysgrid=Ext.getCmp('sysgrid');
			    		sysgrid.loadNewStore(sysgrid,{_noc:1,parentid:record.data.id});
			    		sysgrid.show();
			    	}else {
			    		//需要重新发送请求 加载数据
			    		var params={
			    				caller:'SysCheckData',
			    				condition: 'scd_orid='+record.data.id.replace(/org/g,"")
			    		};
			    		var sysgrid=Ext.getCmp('sysgrid');
			    		sysgrid.loadNewStore(sysgrid,{_noc:1,parentid:record.data.id,type:'employee'});
			    		sysgrid.show();
			    		var grid=Ext.getCmp('gridpanel');
			    		grid.hide();
			    		grid.store.clearFilter(true);
			    		me.loadNewStore(grid,params);			   
			    	}
			   }
   			 }
   			},
   			'button[id=scan]':{
   				'click':function(btn){
   					var f=Ext.getCmp('date');
   					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					var condition="scd_indate "+f.value;
   				    
   				}
   			},
   			'button[id=turnpunish]':{
   				'click':function(btn){
   					Ext.Ajax.request({
   						url:basePath+'ma/SysCheck/TurnReandpunish.action',
   				        method:'post',
   				        params:{
   				        	data:data
   				        },
   				        callback:function(options,success,response){
   				        	var local=new Ext.decode(response.responseText);
   				        	if(local.exceptionInfo){
   				        		showError(local.exceptionInfo);
   				        		return
   				        	}
   				        	if(local.success){
   				        		saveSuccess();
   				        	}
   				        }
   					});
   				}
   			},
   			'erpSysCheckGrid':{
   				itemclick:function(selModel,record){
   					if(!Ext.getCmp('win')){
   					if(record&&record.data.details=='autoshow'&&(record.data.warncount>0||record.data.publishcount>0)){
 					var win = new Ext.window.Window({
 						id:'win',
 						title: "添加特征明细",
 						height: "70%",
 						width: "80%",
 						maximizable : false,
 						buttonAlign : 'center',
 						layout : 'anchor',
 						items: [{
 							xtype: 'erpSysCheckWinGrid',
 							anchor: '100% 100%', 
 							id:'grid3',
 							condition: 'scd_emid='+record.data.orgid +" AND scd_indate "+Ext.getCmp('date').value,
 							readOnly: false,
 							caller:'SysCheckData',
 						}],
 						bbar: ['->',{
 								xtype:'button',
 								text:'生成惩罚单',
 								cls: 'x-btn-gray',
 								iconCls: 'x-button-icon-save',
 								handler:Ext.getCmp('turnpunish').handler
 							},{
 	 							xtype:'erpCloseButton',
 	 	            				style: {
 	 	            					marginLeft: '14px'
 	 	            				},	 	       
 	 							handler:function(){
 	 								Ext.getCmp('win').close();
 	 							}
 	 							},'->']
 						
 						});
 					win.show();
   					}
   					}
   				}
   			},
   			'condatefield':{
   				afterrender:function(field){
   					field.setDateFieldValue(2);
   				}
   			}
    	});
    },
    loadNewStore: function(grid, param){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(!res.data || res.data.length == 0){
        			grid.store.removeAll();
        		} else {
        			grid.store.loadData(data);
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		me.setToolbarValue(grid);
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });
	},
	getParamData:function(){
		var grid=Ext.getCmp('gridpanel');
	    var items=grid.store.data.items;
	    var data=new Array();
	    for(var i=0;i<items.length;i++){
	    	var o=new Object();
	    	if(items[i].data.scd_ispunished==0&&items[i].data.scd_id!=0&&items[i].data.scd_id!='0'&&items[i].data.scd_method==-1){
	    		o.scd_id=items[i].data.scd_id;
	    		o.scd_punishamount=items[i].data.scd_punishamount;
	    		o.scd_title=items[i].data.scd_title;
	    		o.scd_emname=items[i].data.scd_emname;
	    		o.scd_sourcecode=items[i].data.scd_sourcecode;
	    		data.push(o);
	    	}
	    }
	    return  unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
	},
	setToolbarValue:function(grid){
		var store=grid.getStore();
		var warncount=0,publishcount=0,publishamount=0,count=0;
	   for(var i=0;i<store.data.items.length;i++){
		   count++;
		   if(store.data.items[i].data.scd_method=='-1'){
			   publishcount++;
			   publishamount+=store.data.items[i].data.scd_punishamount;
		   }else if(store.data.items[i].data.scd_method=='0'){
			   warncount++;
		   }
	   }
	   Ext.getCmp('scd_count').setText('记录总数:' + count);
	   Ext.getCmp('scd_warn_sum').setText('提醒:'+warncount);
	   Ext.getCmp('scd_publish_sum').setText('扣分:'+publishcount);
	   Ext.getCmp('scd_publishamount_sum').setText('扣分总数:'+publishamount);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
		me.FormUtil.beforeSave(me);
	}
});