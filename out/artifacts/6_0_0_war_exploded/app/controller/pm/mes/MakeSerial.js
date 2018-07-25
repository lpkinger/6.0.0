Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.MakeSerial', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.MakeSerial','core.form.Panel','core.grid.Panel2','core.button.PrintRepair',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn', 'core.button.VastOccur',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.PrintAll','core.button.PrintCombine'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
			},
    		'dbfindtrigger[name=mc_code]' : {
    			afterrender: function(v) {
					var mcid = Ext.getCmp('mc_id').value;
					if (mcid != null & mcid != '') {
						var params = {
						   	caller: 'MakeSerial',
						   	condition : ('ms_mcid=' + mcid || '1=1')
						};
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					}
				},
				aftertrigger : function(v) {
					var mcid = Ext.getCmp('mc_id').value;
					var dbtn = Ext.getCmp('deletebutton'), ubtn = Ext.getCmp('updatebutton'),
					vbtn = Ext.getCmp('erpVastOccurButton'),pabtn1 = Ext.getCmp('printCombine'),
					pabtn2 = Ext.getCmp('printAll'),prbtn = Ext.getCmp('printRepair');
					dbtn.setDisabled(false);
					ubtn.setDisabled(false);					
					vbtn.setDisabled(false);
					pabtn1.setDisabled(false);
					pabtn2.setDisabled(false);
					prbtn.setDisabled(false);
					if (mcid != null & mcid != '') {
						var params = {
								caller: 'MakeSerial',
							    condition : ('ms_mcid=' + mcid || '1=1')
						};
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					}
				}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				me.update();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				warnMsg("确认清空?", function(btn){
    					var mcid = Ext.getCmp('mc_id').value;
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/deleteMakeSerial.action',
    	    			   		params: {
    	    			   			id: mcid
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(opt, s, res) {
    	    			   			me.FormUtil.getActiveTab().setLoading(false);//loading...
    	    						var r = Ext.decode(res.responseText);
									if(r.exceptionInfo) {
										showError(r.exceptionInfo);
									}else if (r.success) {
    	    							if (mcid != null & mcid != '') {
    	    								var params = {
    	    										caller: 'MakeSerial',
    	    									    condition : ('ms_mcid=' + mcid || '1=1')
    	    								};
    	    								me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
    	    							}
    	    						}
    	    					}
    	    				});
    					}
    				});
    			},
    			afterrender:function(btn){
    				btn.setWidth(100);
    				btn.setText('清空序列号');
    				if(Ext.isEmpty(Ext.getCmp('mc_id').value)){
    					btn.setDisabled(true);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.update();
    			},
    			afterrender:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('mc_id').value)){
    					btn.setDisabled(true);
    				}
    			}
    		},
    		'erpVastOccurButton': {
    			click: function(btn){
    				var me = this, win = Ext.getCmp('Complaint-win');
    				var pscode = Ext.getCmp('mc_pscode').value,ps_combineqty ;
    				/*if(Ext.isEmpty(pscode)){
    					showError('请先维护作业单排位表编号!');return;
    				}*/
    				win = Ext.create('Ext.Window', {
    					id: 'Complaint-win',
    					title: '批量生成序列号',
    					height: 200,
    					width: 590,
    					items: [{
    						xtype: 'form',
    						height: '100%',
    						width: '100%',
    						bodyStyle: 'background:#f1f2f5;',
    						items: [{
								xtype: 'fieldcontainer',
								defaults: {
									width:190
								},
	    						layout: {
									type: 'table',
									columns: 3
								},
								items: [{
	    							margin: '10 0 0 0',
	    							xtype: 'textfield',
	    							fieldLabel: '序列前缀',
	    							id:'prefixcode',
	    							allowBlank: false,
	    							colspan: 1,
	    							labelWidth:70
	    						},{
	    							margin: '10 0 0 0',
	    							xtype: 'textfield',
	    							fieldLabel: '起始编码',
	    							id:'startno',
	    							allowBlank: false,
	    							colspan: 1,
	    							labelWidth:70
	    						},{
	    							margin: '10 0 0 0',
	    							xtype: 'textfield',
	    							fieldLabel: '序列后缀',
	    							id:'suffixcode',
	    							allowBlank: true,
	    							colspan: 1,
	    							labelWidth:70
	    						},{
	    							margin: '3 0 0 0',
	    							xtype: 'numberfield',
	    							fieldLabel: '产生序列号数量',
	    							id:'number',
	    							allowBlank: false,
	    							emptyText: 'n'	  ,
	    							style:{'padding-top': '15px'}
	    						},{
	    							margin: '3 0 0 0',
	    							xtype: 'textfield',
	    							fieldLabel: '拼板数',
	    							id:'combineqty',
	    							name:'combineqty',
	    							allowBlank: true ,
	    							labelWidth:70,
	    							value:Ext.getCmp('ps_combineqty').value || 0
	    						}]
	    					}],
    						closeAction: 'hide',
    						buttonAlign: 'center',
    						layout: {
    							type: 'vbox',
    							align: 'center'
    						},
    						buttons: [{
    							text: $I18N.common.button.erpConfirmButton,
    							formBind:true,
    							handler: function(btn) {
    								var form = btn.ownerCt.ownerCt,
    									a = form.down('#prefixcode'),
    									b = form.down('#suffixcode'),
    									c = form.down('#startno'),
    									d = form.down('#number');
    									e = form.down('#combineqty');
    								if(form.getForm().isDirty()) {
    									if(!Ext.isNumeric(c.value) || c.value <'0' ){
    										showError("起始编码必须为数值或者大于等于0!");
    										return ;
    									}
    									if(e.value != '' && e.value != null){
    										if(e.value != 0 && (!Ext.isNumeric(e.value) || e.value <'2') ){
    										 showError("拼板数必须为数值或者大于1!");
    										 return ;
    									  }
    									}else{
    										form.down('#combineqty').setValue(0);
    									}
    									me.OccurCode(Ext.getCmp('mc_id').value, a.value, b.value, c.value, d.value,e.value);
    								}
    							}
    						}, {
    							text: $I18N.common.button.erpCloseButton,
    							cls: 'x-btn-gray',
    							handler: function(btn) {
    								btn.up('window').close();
    							}
    						}]
    					}]
    				});
    				win.show();
    			},
    			afterrender:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('mc_id').value)){
    					btn.setDisabled(true);
    				}
    			}
    		} ,
    		'erpPrintCombineButton':{
    			click:function(btn){//打印拼板号
    				var lp_barcaller = 'MakeSerialCombinePrintAll';
    				var items = Ext.getCmp('grid').store.data.items;
    				var bool = true;
    				Ext.each(items, function(item, index){
    					if(item.data.ms_id != '' && item.data.ms_id != 0 && item.data.ms_id != null){   						
    					}else{
    						bool = false;
    					}
    				});
    				if(bool){	    					
	    				var win = new Ext.window.Window({
					    	id : 'win',			  
							maximizable : true,
						    buttonAlign : 'center',
						    layout : 'anchor',
						    title: '打印模板选择',
						    modal : true,
		   				    items: [{
		   				          tag : 'iframe',
					    	      frame : true,
					    	      anchor : '100% 100%',
					    	      layout : 'fit',
		   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami='+lp_barcaller+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
		   				    }]		   				         
		    	       });
		    	       win.show();	
    				}else{
    				   showError('没有需要打印的明细!');
    				}
    			},
    			afterrender:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('mc_id').value)){
    					btn.setDisabled(true);
    				}
    			}
    		},
    		'erpPrintAllButton':{
    			afterrender:function(btn){
    				btn.setText('打印序列号');
    				if(Ext.isEmpty(Ext.getCmp('mc_id').value)){
    					btn.setDisabled(true);
    				}
    			},
    			click :function(btn){//打印序列号
    				var lp_barcaller = 'MakeSerialCodePrintAll';
    				var items = Ext.getCmp('grid').store.data.items;
    				var bool = true;
    				Ext.each(items, function(item, index){
    					if(item.data.ms_id != '' && item.data.ms_id != 0 && item.data.ms_id != null){   						
    					}else{
    						bool = false;
    					}
    				});
	    			if(bool){	 
	    				var win = new Ext.window.Window({
					    	id : 'win',			  
							maximizable : true,
						    buttonAlign : 'center',
						    layout : 'anchor',
						    title: '打印模板选择',
						    modal : true,
		   				    items: [{
		   				          tag : 'iframe',
					    	      frame : true,
					    	      anchor : '100% 100%',
					    	      layout : 'fit',
		   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami='+lp_barcaller +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
		   				    }]
		   				         
		    	       });
		    	         win.show();	
	    			}else{
	    				showError('没有需要打印的明细!');
	    			}
    			}   			
    		},
    		erpPrintRepairButton:{//补打条码
    			afterrender:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('mc_id').value)){
    					btn.setDisabled(true);
    				}
    			},
    			click :function(btn){//打印序列号
    				var me = this;
    				me.createWin();   					    			
    			}   			
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	OccurCode: function(id, a, b, c, d,e) {
		var me = this;
		Ext.getCmp('Complaint-win').setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'pm/mes/makeSerial/occurCode.action',
			params: {
				id: id,
				prefixcode: a,
				suffixcode: b,
				startno   : c,
				number    : d,
				combineqty: e
			},
			callback: function(opt, s, r) {
				Ext.getCmp('Complaint-win').setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					Ext.Msg.alert("提示","生成成功！");
					var params = {
							caller: 'MakeSerial',
						    condition : ('ms_mcid=' + id || '1=1')
					};
					Ext.getCmp('Complaint-win').close();
					me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
				}
			}
		});
	},
	 createWin: function(){
    	var me = this;
    	var win = new Ext.window.Window({  
    		  modal : true,
        	  id : 'win',
        	  height : '35%',
        	  width : '30%',       	 
        	  layout : 'anchor',   
        	  bodyStyle: 'background: #f1f1f1;',
			  bodyPadding:5,			  
        	  items : [{
        	  	anchor: '100% 100%',
                xtype: 'form',
                bodyStyle: 'background: #f1f1f1;',             
	            items:[{
	        	      xtype:'textfield',
	        		  name:'serialCode',
	        		  fieldLabel:'序列号',
	        		  id:'serialCode',
	        		  allowBlank:false,	        		 
	        		  fieldStyle : "background:rgb(224, 224, 255);",    
				  	  labelStyle:"color:red;"	        		  
	        	  },{
				      xtype: 'checkbox',
		              boxLabel : '产生新序列号',
		              name : 'newSerial',
		              checked : false,
		              id   : 'newSerial',
		              fieldStyle:''
		        }],
                buttonAlign : 'center',
	            buttons: [{
					text: '确定'	,
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-save',
					id:'confirmBtn',
					formBind: true, //only enabled once the form is valid
                    handler: function(btn) {                   	                  	
    					me.checkOrNewBarcode();		                                             
					  }
				  },{
				    text: '取消'	,
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-delete',				
                    handler: function(btn) {                   	                  	
    					win.close();	                                             
					  }
				  }]
    	       }]
    		});
    	win.show(); 
    },
    checkOrNewBarcode:function(){//判断是新增条码还是补打原有的条码
    	var me= this , newSerial = Ext.getCmp('newSerial').value,
    	mc_id = Ext.getCmp('mc_id').value,serialCode = Ext.getCmp('serialCode').value;
    	Ext.Ajax.request({
			url: basePath + 'pm/mes/makeSerial/checkOrNewSerialCode.action',
			params: {
				newSerial: newSerial,//是否新生成条码
				serialCode:serialCode,      //需要打印的条码
				mc_id:mc_id           //作业单Id，
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);return;
				} else {
					Ext.getCmp('win').close();
					var params = {
							caller: 'MakeSerial',
						    condition : ('ms_mcid=' + mc_id || '1=1')
					};
					me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					var ms_id = rs.message;
					var lp_barcaller = 'MakeSerialCodePrint';   				
	    			var win = new Ext.window.Window({
					    id : 'win2',			  
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						title: '打印模板选择',
						modal : true,
		   				items: [{
		   				      tag : 'iframe',
					    	  frame : true,
					    	  anchor : '100% 100%',
					    	  layout : 'fit',
		   				      html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami='+lp_barcaller +'&formCondition='+ms_id+'height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
		   				}]		   				         
		    	   });
		    	   win.show();						
				}
			}
		});   	
    },
    update : function(){
    	var me = this;
    	var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var mc_id = Ext.getCmp('mc_id').value,
    		mc_code = Ext.getCmp('mc_code').value,
    		mc_prodcode = Ext.getCmp('mc_prodcode').value;
		Ext.Array.each(items, function(item) {
			if(!Ext.isEmpty(item.data['ms_sncode']) && Ext.isEmpty(item.data['ms_mcid'])){
				item.set('ms_mcid', mc_id);			  
			}
		});
		var form = Ext.getCmp('form');
		var jsondata = me.GridUtil.getGridStore();
		if(jsondata.length == 0){//未修改数据						
		    showError('还未修改或新增数据');
			return ;
		}	
		me.FormUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'pm/mes/updateMakeSerial.action',
        	params: {
        		formStore:unescape(escape(Ext.JSON.encode(form.getValues()))),
        		caller:caller,
        		param:unescape(jsondata)
        	},
        	async: false,
        	method : 'post',
        	callback:function(options,success,response){	
        		me.FormUtil.getActiveTab().setLoading(false);
				var res = Ext.decode(response.responseText);
    			if(res.exceptionInfo){
    				showError(res.exceptionInfo);
    				return;
				}else{
					var params = {
					   caller: 'MakeSerial',
				       condition : ('ms_mcid=' + mc_id || '1=1')
					};
					me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
		  	    }
            }
        });	    				
    }
});