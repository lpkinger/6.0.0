Ext.QuickTips.init();
Ext.define('erp.controller.plm.record.BillRecord', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.record.BillRecord','core.grid.Panel2','core.toolbar.Toolbar','core.form.Panel',
    		'core.button.Close','core.button.Over','core.button.Update','core.button.DeleteDetail',
    		'core.button.ChangeHandler','core.trigger.DbfindTrigger','core.form.FileField','core.button.Confirm',
    		'core.button.NoConfirm'
    	],
    init:function(){
    	var me = this;
    	me.control({  
    		'erpFormPanel': {
			   afterrender:function(form){
				   Ext.defer(function(){
					   var ra_id=form.down('#ra_id').getValue(),
					    status=form.down('#ra_statuscode').getValue();
					   if(ra_id){
						   var data= me.getWorkRecord(ra_id);
						   if(data.length>0){
						   var index=me.getInsertIndex();
						   form.insert(index,{
							   xtype: 'fieldset',
							   title: '<h2><img src="' + basePath + 'resource/images/icon/communicate.png" width=20/>处理明细</h2>',
							   collapsible: true,
							   //collapsed: true,
							   columnWidth:1,
							   layout: 'anchor',
							   defaults: {
								   anchor: '100%',
								   labelStyle: 'padding-left:4px;'
							   },
							   items:[{
								   autoScroll: true,
								   xtype: 'dataview',
								   tpl:Ext.create('Ext.XTemplate',
										   '<tpl for=".">',
										   '<div class="search-item">',
										   '<h3><span>{WR_RECORDDATE}<br /> {WR_RECORDER}</span>',
										   '<font color="blue">{WR_PROGRESS:this.formatKind} &nbsp;&nbsp;</font></h3>',
										   '<p><font color="green">回复信息:</font> {WR_REDCORD}</p>',
										   '</div></tpl>',
										   {formatKind: function(value){
										   if(value=='reply') return '回复处理';
										   else if(value=='confirm') return '确认处理';
										   else if(value=='noconfirm') return '驳回处理';}
										   }),
								   store: Ext.create('Ext.data.Store', {
									   fields:[{name: 'WR_RECORDER' },
									           {name: 'WR_RECORDDATE'},
									           {name: 'WR_PROGRESS'},
									           {name: 'WR_REDCORD'}],
									           data:data
								   })  ,
								   itemSelector:'div.search-item'
							   }]
						   });
						   }
						   if(status!='FINISHED')
						   form.add(me.createRecord());
					   }

				   },700);

			   }
		   },
    		'field[name=sourcecode]':{
  			   afterrender:function(f){
  				   if(f.value!=null && f.value!=''){
  				   f.setFieldStyle({
  					   'color': 'red'
  				   });
  				   f.focusCls = 'mail-attach';
  				   var c = Ext.Function.bind(me.openSource, me);
  				   Ext.EventManager.on(f.inputEl, {
  					   mousedown : c,
  					   scope: f,
  					   buffer : 100
  				   });
  				  }
  			   }
 			},
 			'erpUpdateButton' : {
 				click : function(b) {
 					var form = b.ownerCt.ownerCt,
 						f = form.down('#wr_redcord');
 					if(f && !Ext.isEmpty(f.getValue())) {
 						me.onUpdate(form);
 					}
 				},
 				afterrender:function(btn){
                	var status=Ext.getCmp('ra_status');
                	if(status.value == '待确认' || status.value =='已结束'){
                		btn.hide();
                	}
                }
 			},
 			'erpChangeHandlerButton' : {
 				afterrender : function(b) {
 					var form = b.ownerCt.ownerCt,
 						status = form.down('#ra_statuscode');
 					if(status && (status.getValue() == 'FINISHED' ||  status.getValue()=='UNCONFIRMED')) {
 						b.hide();
 					} else {
 						b.setDisabled(false);
 						b.show();
 					}
 				},
 				click : function(b) {
 					warnMsg('确定变更该任务?', function(k){
 						if(k == 'yes' || k == 'ok') {
 							var form = b.ownerCt.ownerCt;
 							me.onHandlerChange(form);
 						}
 					});
 				}
 			},
 			'erpOverButton' : {
 				click : function(b) {
 					warnMsg('确定已达到提出人要求,结束该任务?', function(k){
 						if(k == 'yes' || k == 'ok') {
 							var form = b.ownerCt.ownerCt;
 		 					me.onOver(form);
 						}
 					});
 				},
 				afterrender:function(btn){
                	var status=Ext.getCmp('ra_status'),resourcename=Ext.getCmp('ra_resourcename').getValue();
                	btn.setText('回复');
                	if(status.value  =='待确认' || status.value =='已完成' || resourcename!=recorder){
                		btn.hide();
                	}
                }
 			},
 			'erpConfirmButton' : {
                afterrender:function(btn){
                	var status=Ext.getCmp('ra_status'),exhibitor=Ext.getCmp('recorder');
                	if(status.value  !='待确认' || exhibitor.value !=recorder || status =='已完成'){
                		btn.hide();
                	}
                },
                click : function(b) {
 					var form = b.ownerCt.ownerCt;
 		 			me.onConfirm(form);
 				}
 			},
 			'erpNoConfirmButton' : {
                afterrender:function(btn){
                	var status=Ext.getCmp('ra_status'),exhibitor=Ext.getCmp('recorder');
                	if(status.value  !='待确认' || exhibitor.value !=recorder || status.value =='已完成'){
                		btn.hide();
                	}
                },
                click : function(b) {
 					var form = b.ownerCt.ownerCt;
 		 			me.onNoConfirm(form);
 				}
 			}
 			
    	});
    },
    openSource : function(e, el, obj) {
    	var f = obj.scope;
    	if(f.value) {
    		this.FormUtil.onAdd(null, f.ownerCt.down('#sourcecode').value, 
    				f.ownerCt.down('#sourcelink').value + '&_noc=1');
    	}
    },
    getInsertIndex:function(){
 	   var form=Ext.getCmp('form'),i=0;
 	   Ext.Array.each(form.items.items,function(item,index){
 		   if(item.name=='description'){
 			   i=index;
 		   }
 	   });
 	   return i+1;
    },
    getWorkRecord : function(id, form) {
    	var me = this,data=[];
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'WorkRecord',
	   			fields: 'wr_recorder,wr_recorddate,wr_redcord,WR_PROGRESS',
	   			condition: 'wr_raid=' + id + ' order by wr_recorddate'
	   		},
	   	    async:false,
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
/*	   			var status = form.down('#ra_statuscode');
	   			//alert(status.getValue());
	   			if(status && (status.getValue() != 'FINISHED' || status.getValue() != 'UNCONFIRMED')) {
	   				//form.down('erpUpdateButton').show();
	   				//form.down('erpOverButton').show();
	   				form.add(me.createRecord());
	   			} else {
	   				form.down('erpOverButton').hide();
	   				//form.down('erpConfirmButton').hide();
	   			}*/
    			if(r.success && r.data){
    				data= new Ext.decode(r.data);
	   			}
	   		}
		});
		return data;
    },
    createRecord : function(r, t, v) {
    	var args = {columnWidth : 1};
    	if (r) {
    		args.fieldLabel = Ext.Date.format(Ext.Date.parse(t,'Y-m-d H:i:s'),'m-d H:i:s') + '<br>' + r;
    		args.value = v;
    		args.readOnly = true;
    		args.fieldStyle = 'background:#f1f1f1;';
    		args.labelSeparator = '';
    	} else {
    		args.fieldLabel = '回复信息';
    		args.name = 'wr_redcord';
    		args.id = 'wr_redcord';
    		args.labelAlign='top';
    		args.cls = 'form-field-allowBlank';
    	}
    	return Ext.create('Ext.form.field.TextArea', args);
    },
    onUpdate : function(form) {
    	var id = form.down('#ra_id').getValue(),
    		text = form.down('#wr_redcord').getValue();
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url : basePath + 'plm/record/updateBillRecord.action',
    		params : {
    			_noc : 1,
    			caller : caller,
    			wr_raid : id,
    			wr_redcord : text
    		},
    		callback : function(opt, s, res) {
    			form.setLoading(false);
    			var r = Ext.decode(res.responseText);
    			if (r.success) {
    				showMessage('提示', '保存成功!', 1000);
    				window.location.reload();
    			} else if(r.exceptionInfo) {
    				showError(r.exceptionInfo);
    			}
    		}
    	});
    },
    onHandlerChange : function(form) {
    	var me = this;
    	var win = Ext.create('Ext.window.Window', {
    		width : 300,
    		height : 150,
    		cls : 'custom-blue',
    		title : '变更处理人',
    		bodyStyle : 'background:#f1f2f5;',
    		layout : {
    			type : 'vbox',
    			align : 'center',
    			pack : 'center'
    		},
    		items : [{
    			xtype : 'dbfindtrigger',
    			name : 'ma_recorder',
    			labelWidth : 75,
    			fieldLabel : '新执行人',
    			listeners : {
					aftertrigger : function(t, r) {
						t.setValue(r.get('em_name'));
						t.em_id = r.get('em_id');
					}
				}
    		}],
    		buttonAlign : 'center',
    		buttons : [{
    			text : '确认变更',
    			cls : 'x-btn-blue',
    			handler : function(b){
    				var w = b.ownerCt.ownerCt,
    					e = w.down('dbfindtrigger');
    				if(e.em_id) {
    					me.changeHandler(form, e.em_id);
    					w.close();
    				}
    			}
    		},{
    			text : '取消',
    			cls : 'x-btn-blue',
    			handler : function(b) {
    				b.ownerCt.ownerCt.close();
    			}
    		}]
    	});
    	win.show();
    },
    changeHandler : function(form, em_id) {
    	var me = this, id = form.down('#ra_id').getValue();
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url : basePath + 'plm/record/changeBillTask.action',
    		params : {
    			caller : caller,
    			_noc : 1,
    			ra_id : id,
    			em_id : em_id
    		},
    		callback : function(opt, s, res) {
    			form.setLoading(false);
    			var r = Ext.decode(res.responseText);
    			if (r.success) {
    				showMessage('提示', '变更成功!', 1000);
    				me.BaseUtil.getActiveTab().close();
    			} else if(r.exceptionInfo) {
    				showError(r.exceptionInfo);
    			}
    		}
    	});
    },
    onOver : function(form) {
    	var id = form.down('#ra_id').getValue();
    	text = form.down('#wr_redcord').getValue();
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url : basePath + 'plm/record/endBillTask.action',
    		params : {
    			caller : caller,
    			_noc : 1,
    			ra_id : id,
    			record:text
    		},
    		callback : function(opt, s, res) {
    			form.setLoading(false);
    			var r = Ext.decode(res.responseText);
    			if (r.success) {
    				showMessage('提示', '结束成功!', 1000);
    				window.location.reload();
    			} else if(r.exceptionInfo) {
    				showError(r.exceptionInfo);
    			}
    		}
    	});
    },
    onConfirm : function(form) {
    	var id = form.down('#ra_id').getValue();
    	text = form.down('#wr_redcord').getValue();
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url : basePath + 'plm/record/confirmBillTask.action',
    		params : {
    			caller : caller,
    			_noc : 1,
    			ra_id : id,
    			record:text
    		},
    		callback : function(opt, s, res) {
    			form.setLoading(false);
    			var r = Ext.decode(res.responseText);
    			if (r.success) {
    				showMessage('提示', '确认成功!', 1000);
    				window.location.reload();
    			} else if(r.exceptionInfo) {
    				showError(r.exceptionInfo);
    			}
    		}
    	});
    },
    onNoConfirm:function(form) {
    	var id = form.down('#ra_id').getValue();
    	text = form.down('#wr_redcord').getValue();
    	form.setLoading(true);
    	Ext.Ajax.request({
    		url : basePath + 'plm/record/noConfirmBillTask.action',
    		params : {
    			caller : caller,
    			_noc : 1,
    			ra_id : id,
    			record:text
    		},
    		callback : function(opt, s, res) {
    			form.setLoading(false);
    			var r = Ext.decode(res.responseText);
    			if (r.success) {
    				showMessage('提示', '驳回成功!', 1000);
    				var main = parent.Ext.getCmp("content-panel"); 
    				if(main) main.getActiveTab().close();
    			} else if(r.exceptionInfo) {
    				showError(r.exceptionInfo);
    			}
    		}
    	});
    }
});