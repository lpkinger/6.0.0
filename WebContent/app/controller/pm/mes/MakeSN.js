Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.MakeSN', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.MakeSN','core.form.Panel','core.grid.Panel2','core.button.PrintRepair',
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
    		'dbfindtrigger[name=ma_code]' : {
    			afterrender: function(v) {
					var maid = Ext.getCmp('ma_id').value;
					if (maid != null & maid != '') {
						var params = {
						   	caller: 'MakeSN',
						   	condition : ('msl_maid=' + maid || '1=1')
						};
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					}
				},
				aftertrigger : function(v) {
					var maid = Ext.getCmp('ma_id').value;
					var dbtn = Ext.getCmp('deletebutton'),vbtn = Ext.getCmp('erpVastOccurButton'),
					pabtn1 = Ext.getCmp('printAll'),prbtn = Ext.getCmp('printRepair');
					dbtn.setDisabled(false);
					vbtn.setDisabled(false);
					pabtn1.setDisabled(false);
					prbtn.setDisabled(false);
					if (maid != null & maid != '') {
						var params = {
								caller: 'MakeSN',
							    condition : ('msl_maid=' + maid || '1=1')
						};
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					}
				}
			},
    		'erpDeleteButton': {
    			click: function(btn){
    				warnMsg("确认清空?", function(btn){
    					var maid = Ext.getCmp('ma_id').value;
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/deleteMakeSN.action',
    	    			   		params: {
    	    			   			id: maid
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(opt, s, res) {
    	    			   			me.FormUtil.getActiveTab().setLoading(false);//loading...
    	    						var r = Ext.decode(res.responseText);
									if(r.exceptionInfo) {
										showError(r.exceptionInfo);
									}else if (r.success) {
    	    							if (maid != null & maid != '') {
    	    								var params = {
    	    										caller: 'MakeSN',
    	    									    condition : ('msl_maid=' + maid || '1=1')
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
    				if(Ext.isEmpty(Ext.getCmp('ma_id').value)){
    					btn.setDisabled(true);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpVastOccurButton': {
    			click: function(btn){
    				var me = this, win = Ext.getCmp('Complaint-win');
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
	    							minValue:0,
	    							style:{'padding-top': '15px'}
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
    								if(form.getForm().isDirty()) {
    									if(!Ext.isNumeric(c.value) || c.value <'0' ){
    										showError("起始编码必须为数值或者大于等于0!");
    										return ;
    									}
    									me.OccurCode(Ext.getCmp('ma_id').value, a.value, b.value, c.value, d.value);
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
    				if(Ext.isEmpty(Ext.getCmp('ma_id').value)){
    					btn.setDisabled(true);
    				}
    			}
    		} ,
    		'erpPrintAllButton':{
    			afterrender:function(btn){
    				btn.setText('打印序列号');
    				if(Ext.isEmpty(Ext.getCmp('ma_id').value)){
    					btn.setDisabled(true);
    				}
    			},
    			click:function(btn){
    				var me = this;
    				me.firstPrint(false,0);
    			}
    		},
    		erpPrintRepairButton:{//补打条码
    			afterrender:function(btn){
    				if(Ext.isEmpty(Ext.getCmp('ma_id').value)){
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
	OccurCode: function(id, a, b, c, d) {
		var me = this;
		Ext.getCmp('Complaint-win').setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'pm/mes/makeSN/occurCode.action',
			params: {
				id: id,
				prefixcode: a,
				suffixcode: b,
				startno   : c,
				number    : d
			},
			callback: function(opt, s, r) {
				Ext.getCmp('Complaint-win').setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					Ext.Msg.alert("提示","生成成功！");
					var params = {
							caller: 'MakeSN',
						    condition : ('msl_maid=' + id || '1=1')
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
    	ma_id = Ext.getCmp('ma_id').value,serialCode = Ext.getCmp('serialCode').value;
    	Ext.Ajax.request({
			url: basePath + 'pm/mes/makeSN/checkOrNewSerialCode.action',
			params: {
				newSerial: newSerial,//是否新生成条码
				serialCode:serialCode,      //需要打印的条码
				ma_id:ma_id         //作业单Id，
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);return;
				} else {
					Ext.getCmp('win').close();
					var params = {
							caller: 'MakeSN',
						    condition : ('msl_maid=' + ma_id || '1=1')
					};
					me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					var msl_id = rs.message;
					me.firstPrint(true,msl_id);
				}
			}
		});   	
    },
    beforePrint: function(f,callback) {
		var me = this;
		if(printType!='jasper'){
    		Ext.Ajax.request({
    			url: basePath + 'common/report/getFields.action',
    			method: 'post',
    			params:{
    				caller:f
    			},
    			callback: function(opt, s, r) {
    				var rs = Ext.decode(r.responseText);    				
    				callback.call(null,rs);
    			}
    		});
		}else{
			Ext.Ajax.request({
    			url: basePath + 'common/JasperReportPrint/getFields.action',
    			method: 'post',
    			params:{
    				caller:f
    			},
    			callback: function(opt, s, r) {
    				var rs = Ext.decode(r.responseText);    				
    				callback.call(null,rs);
    			}
			});
		}
	},
    firstPrint: function(isRepair,id){
    	var me = this;
    	if(printType=='jasper'){
	    	me.beforePrint(caller,function(data){
				if(data.datas.length>1){   				
					Ext.create('Ext.window.Window', {
						autoShow: true,
						title: '选择打印类型',
						id:isRepair?'repair':'normal',
						width: 400,
						height: 300,
						layout: 'anchor',
						items: [{ 							    					
							  anchor:'100% 100%',
							  xtype:'form',
							  id :isRepair?'repair':'printall',
							  buttonAlign : 'center',
							  items:[{
							        xtype: 'combo',
									id: 'template',
									fieldLabel: '选择打印类型', 									
									store: Ext.create('Ext.data.Store', {
										autoLoad: true,
									    fields: ['TITLE','ID','REPORTNAME'],
									    data:data.datas 									 
									}),
									queryMode: 'local',
								    displayField: 'TITLE',
								    valueField: 'ID',
									width:300,
								    allowBlank:false,
								    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
									style:'margin-left:15px;margin-top:15px;',
									listeners : {
								      afterRender : function(combo) {
								         combo.setValue(data.datas[0].ID);
								      }
								   }
								}]	 							    	     				    							           	
						 }], 
						buttonAlign: 'center',
						buttons: [{
							text: '确定',
							handler: function(b) {
								var temp = Ext.getCmp('template');
								if(temp &&  temp.value!= null){
									var selData = temp.valueModels[0].data;
									if(isRepair){
										me.printRepair(caller,selData.REPORTNAME,id);
									}else{
										me.printAll(caller,selData.REPORTNAME);
									}
								}else{
									alert("请选择打印模板");
								}   								
							}
						}, {
							text: '取消',
							handler: function(b) {
								b.ownerCt.ownerCt.close();
							}
						}]
					});
			}else{
				me.printRepair(caller,data.datas[0].REPORTNAME,id);
			}    			
		});
    	}
    },
    printRepair : function(caller,reportname,id){
    	var me = this;
    	var params = new Object();
    	var ma_id = Ext.getCmp('ma_id').value;
    	params['idS'] = id;
    	params['isRepair'] = true;
    	Ext.Ajax.request({
	    	url : basePath +'common/JasperReportPrint/print.action',
			params: {
				params: unescape(escape(Ext.JSON.encode(params))),
				caller:caller,
				reportname:reportname
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					if (ma_id != null & ma_id != '') {
						var params = {
						   	caller: 'MakeSN',
						   	condition : ('msl_maid=' + ma_id || '1=1')
						};
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					}
					var printcondition = '(MAKESNLIST.MSL_id='+id+') ';
					printcondition = res.info.whereCondition=='' ? 'where '+printcondition :'where '+res.info.whereCondition+' and '+printcondition;
					var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+printcondition+'&otherParameters=&printType='+res.info.printtype;
					window.open(url,'_blank');
				}else if(res.exceptionInfo){
					var str = res.exceptionInfo;
					showError(str);return;
				}
			}
	    });
    },
    printAll : function(caller,reportname){
    	var me = this;
    	var params = new Object();
    	var ma_id = Ext.getCmp('ma_id').value
    	params['idS'] = ma_id;
    	params['isRepair'] = false;
    	Ext.Ajax.request({
	    	url : basePath +'common/JasperReportPrint/print.action',
			params: {
				params: unescape(escape(Ext.JSON.encode(params))),
				caller:caller,
				reportname:reportname
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					if (ma_id != null & ma_id != '') {
						var params = {
						   	caller: 'MakeSN',
						   	condition : ('msl_maid=' + ma_id || '1=1')
						};
						me.GridUtil.loadNewStore(Ext.getCmp('grid'), params);
					}
					var printcondition = '(MAKE.MA_id='+ma_id+') ';
					printcondition = res.info.whereCondition=='' ? 'where '+printcondition :'where '+res.info.whereCondition+' and '+printcondition;
					var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+printcondition+'&otherParameters=&printType='+res.info.printtype;
					window.open(url,'_blank');
				}else if(res.exceptionInfo){
					var str = res.exceptionInfo;
					showError(str);return;
				}
			}
	    });
    }
});