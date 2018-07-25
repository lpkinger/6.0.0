Ext.define('erp.view.common.subsFormula.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.subsForm',
	hideBorders: true, 
	id:'form',
	title:null,
	autoWidth : true,
	bodyPadding:'15 0 0 10',
	bodyStyle: 'background: #f2f2f2;',
	autoScroll:true,
	enableTools: false,
	readOnly:false,
	layout: 'column', 
	initComponent : function(){
		var me = this;
    	me.FormUtil=Ext.create('erp.util.FormUtil');
    	me.GridUtil=Ext.create('erp.util.GridUtil');
    	me.BaseUtil=Ext.create('erp.util.BaseUtil');
		formCondition = getUrlParam('formCondition');
		if(formCondition){
			this.getData();
		}
		this.callParent(arguments); 
		if(this.enableTools) {
			me.setTools();
		}
	},
	defaults:{
		xtype:'textfield',
		focusCls: 'x-form-field-cir-focus',
		columnWidth:0.25,
		/*cls: "form-field-allowBlank", */
		labelAlign: "right" ,
		margin:'5 0 0 0',
		labelWidth:70
	},
		items:[{
			name:'id_',
			fieldLabel:'ID',
			id : 'id_',
			hidden:true,
			xtype:'numberfield'
	    },{ 
		    name:'code_',
		    fieldLabel:'流水号',
		    id : 'code_',
		    readOnly:true,
		    fieldStyle:'background:#e0e0e0;'
		},{
		    name:'title_',
		    labelStyle:"color:null;",
		    fieldLabel:'<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>标题',
		    id : 'title_',
		    allowBlank : false
		},{
			name:'before_',
			fieldLabel:'执行前',
			id : 'before_',
			readOnly:false
	    },{
	    	name:'style_',
	    	id : 'style_',
			labelStyle:"color:null;",
            xtype : 'combo',
            combineErrors: true,
            msgTarget: 'side',
            editable:       false,
            displayField:   'name',
            valueField:     'value',
            fieldLabel: '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>展现方式',
			allowBlank : false,
            defaults: {
                hideLabel: true
            },
            store:Ext.create('Ext.data.Store', {
                        fields : ['name', 'value'],
                        data   : [
                            {name : '饼图',   value: 'pie'},
                            {name : '条形图',  value: 'bar'},
                            {name : '列表', value: 'list'},
                            {name : '柱状图', value: 'column'},
                            {name : '线形图', value: 'line'},
                            {name : '信息汇总', value: 'sum'}
                        ]
              })
        },{
			name:'desc_',
			id:'desc_',
			/*xtype:'textarea',*/
			fieldLabel:'描述'
	    },{
			name:'valuefield_',
			id : 'valuefield_',
			fieldLabel:'值字段',
			value:'yField',
			/*readOnly: true,
			fieldStyle:'background:#e0e0e0;'*/
	    },{
			name:'valuedisplay_',
			id : 'valuedisplay_',
			fieldLabel:'值描述',
			width:400
	    },{
			name:'keyfield_',
			id : 'keyfield_',
			fieldLabel:'参照字段',
			/*readOnly: true,
			fieldStyle:'background:#e0e0e0;',*/
			value:'xField'
	    },{
			name:'keydisplay_',
			id : 'keydisplay_',
			fieldLabel:'参照描述'
	    },{
			name:'unit_',
			id : 'unit_',
			fieldLabel:'单位'
	    },{
			name:'status_',
			id : 'status_',
			fieldLabel:'状态',
			readOnly: true,
			fieldStyle:'background:#e0e0e0;',
			value:'在录入'
	    },{
			name:'statuscode_',
			id : 'statuscode_',
			fieldLabel:'状态码',
			hidden:true
	    },{
	    	name:'isapplied_',
			id:'isapplied_',
			labelStyle:"color:null;",
            xtype : 'combo',
            msgTarget: 'side',
            fieldLabel: '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>允许申请',
            value:-1,
            hidden:true,
			allowBlank : false,
			displayField:   'name',
			editable:false,
 			valueField:     'value',
            defaults: {
                hideLabel: true
            },
            store: Ext.create('Ext.data.Store', {
                        fields : ['name', 'value'],
                        data   : [
                            {name : '是',   value: -1},
                            {name : '否',   value: 0}
                        ]
             })
        },{
			name:'checked_',/*给是否检测通过赋值*/
			id:'checked_',
			fieldLabel:'检测通过',
			columnWidth:0.249,
			readOnly: true,
			fieldStyle:'background:#e0e0e0;',
			xtype : 'combo',
            msgTarget: 'side',
			displayField:   'name',
			editable:false,
 			valueField:     'value',
            defaults: {
                hideLabel: true
            },
            store: Ext.create('Ext.data.Store', {
                        fields : ['name', 'value'],
                        data   : [
                            {name : '是',   value: -1},
                            {name : '否',   value: 0}
                        ]
             })
	    },{
			name:'sql_',
			id : 'sql_',
			labelStyle:"color:null;",
			fieldLabel:'<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>SQL语句',
			allowBlank : false,
			/*xtype:'textareatrigger',*/
			xtype:'textarea',
			height:160,
			autoScroll:true,
			columnWidth:1
	    },{
	       xtype:'htmleditor',	    
	       margin:'5 0 0 0',
	       height:170,
	       fieldLabel:'简介说明',
	       id:'intro_',
	       name:'intro_',
	       columnWidth:0.6
	    },{
              id:'fileform',
              columnWidth:0.4,
              minWidth:200,
              xtype:'form',
              margin:'5 0 0 0',
              baseBodyCls:'x-form-item-body u_container',
              bodyStyle:'background:#f2f2f2',
              items:[{
                  name:'img_',               
                  id:'img_',
                  fieldLabel:'简图设置',
                  labelWidth:70,
                  xtype: 'filefield',
                  name: 'file',
                  buttonOnly:true,
                  createFileInput : function() {
	                   var a = this;
	                    a.fileInputEl = a.button.el.createChild({
				           name : a.getName(),
				           cls : Ext.baseCSSPrefix + "form-file-input",
				           tag : "input",
				           type : "file",
				           size : 1,
				           accept:"image/*"
			           }).on("change", a.onFileChange, a)
                   },
                  buttonConfig:{
                      text:'上传',
                      margin:'2 0 0 2',
                      iconCls:'x-button-icon-pic'
                  },
                  listeners: {
                      change: function(field){
                          field.ownerCt.upload(field);
                      }
                  }
              },{
                  xtype: 'image',
                  width:  75,
                  height: 75,
                  id:'logo',
                  margin:'2 0 0 75',
                  listeners:{
                  	 'afterrender':function(img,opts){
                  	 	if(formCondition){
                  	 		var id=formCondition.split("=")[1];  
                            img.setSrc(basePath+'common/charts/getImage.action?table=subsformula&id='+id);
                  	 	}
                  	 }
                  }
              },{
                  xtype: 'displayfield',
                  margin:'2 0 0 75',
                  value: '<div style="color:gray">建议尺寸75*75像素左右图片</div>'
              }],
               upload: function(field){
               	 var img_reg= /\.([jJ][pP][gG]){1}$|\.([jJ][pP][eE][gG]){1}$|\.([gG][iI][fF]){1}$|\.([pP][nN][gG]){1}$|\.([bB][mM][pP]){1}$/;
				 if (img_reg.test(field.value)) {
                      field.ownerCt.getForm().submit({
                          url: basePath + 'common/charts/saveimage.action',
                          waitMsg:'正在上传',
                          params:{
                          		id:Ext.getCmp('id_').getValue(),
                          		table:'subsformula'
                          },
                          success: function(fp, o){
                          	 var logo=Ext.getCmp('logo');
                          	 logo.fireEvent('afterrender',logo);   
              				if(o.result.success){
                              Ext.Msg.alert('提示','上传成功');  
                              logo.show();
                              window.location.href = window.location.href;
                      			}else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                          },
                          failure:function(fp,o){
                              Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
                          }
                      });  
				 }else{
				 	 Ext.Msg.alert('提示', '文件类型错误,请选择图片文件(jpg/jpeg/gif/png/bmp)');
				 }
              }
            }],
	buttonAlign:'center',
	tbar:{margin:'0 0 5 0',style:{background:'#fff'},items:[{
		xtype: 'erpAddButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpDeleteButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpSaveButton',
		
		hidden:!Ext.isEmpty(formCondition)
	},{
		xtype: 'erpUpdateButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpSubmitButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpResSubmitButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpAuditButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpResAuditButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpTestButton',
		hidden:Ext.isEmpty(formCondition)
	},{
		margin:'0 0 0 5',
	    xtype: 'button',
		name: 'preview',
		id: 'preview',
		text: '预览',						
		cls: 'x-btn-gray',																				
		iconCls: 'x-button-icon-preview',
		width:60,
		hidden:Ext.isEmpty(formCondition)
	},{
		margin:'0 0 0 5',
		cls: 'x-btn-gray',	
		iconCls: 'x-nbutton-icon-report',
		text: '单据设置',
		listeners:{
			afterrender:function(v){
				var me = this.ownerCt.ownerCt;
				me.ifadmin(v);							
			},
			click:function(){
				var me = this.ownerCt.ownerCt;
				me.reportset();
			}
		}
	},'->',{
		margin:'0',
		xtype: 'erpCloseButton',
		id:'close'
	}]},
	getData:function(){
		var me = this;
		//从url解析参数
		if(formCondition != null && formCondition != '')
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=").replace(/\'/g,"");
			this.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'common/charts/getSubsData.action',
	        	params: {
	        		caller: caller, 
	        		id: formCondition.split("=")[1],
	        		_noc: (getUrlParam('_noc') || me._noc)
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		getbyUUid=false;
	        		me.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else{
	        			me.setFormValues(res.data);
	        		}
	        	}
	        });
	},
	setTools: function(){
		var me = this, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
			hasVoucher = !!me.voucherConfig, dumpable = me.dumpable,
			isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;
		me.tools = [
					{	xtype:'button',				
						text:'选项',
						id:'buttons',
						margin:'0 0 0 2',					
						menu: [{
							iconCls: 'x-nbutton-icon-log',
							text: '操作日志',	
							listeners:{	
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click: function(btn){								
									var form = Ext.getCmp('form');
									var id = Ext.getCmp(form.keyField).value;
									if(id != null && id != 0){
										form.getLogs(id);
									}
								}
							}							
						},{
							iconCls: 'x-nbutton-icon-message',
							text: '消息日志',
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(btn){
									var form = Ext.getCmp('form');
									var id = Ext.getCmp(form.keyField).value;
									if(id != null && id != 0){										
									me.getMessageInfo(id,caller);}
								}	
							}							
						},{
							iconCls: 'x-nbutton-icon-link',
							text: '关联查询',
							hidden: !isNormalPage,
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(btn){
									var form = Ext.getCmp('form');
									form.showRelativeQuery();
								}
							}
						},{
							iconCls: 'x-nbutton-icon-download',
							text: '下载数据',
							hidden: !isNormalPage,
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(){
									var form = Ext.getCmp('form');
									var id = Ext.getCmp(form.keyField).value;
									form.saveAsExcel(id,caller);
								}
							}							
						},{
							iconCls: 'x-nbutton-icon-plan',
							text: '导出方案',
							hidden: !dumpable,
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(){
									// 用于配置方案导出
									me.expData();
								}
							}							
						},{
							iconCls: 'x-nbutton-icon-process',
							text: '流程处理',
							hidden: !isNormalPage,
							listeners:{	
								afterrender:function(btn){
									var form = Ext.getCmp('form');
									if(!form.statuscodeField){
										btn.disable(true);
									} else {
										var f = form.statuscodeField;
									if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
										btn.disable(true);
									} }
								},
								click :function(btn){
									var form = Ext.getCmp('form');
									if(!form.statuscodeField){
										btn.disable(true);
									} else {
										var f = form.statuscodeField;
									if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
										btn.disable(true);
									} else {
										var id = Ext.getCmp(form.keyField).value;
										if(id != null && id != 0){
											form.getProcess(id);
								}
							}
						}
					}
							}
				},{
							iconCls: 'x-nbutton-icon-report',
							text: '单据设置',
							listeners:{
								afterrender:function(v){
									me.ifadmin(v);							
								},
								click:function(){
									me.reportset();
								}
							}
							
						}]
					},
		{
			xtype : 'button',
			text:'凭证',
			margin:'0 0 0 2',
			hidden : !hasVoucher,
			listeners : {
				click : function(t) {
					var form = t.ownerCt.ownerCt;
					form.createVoucher(form.voucherConfig);
				}
			}
		},		
		{
			xtype: 'button',
			text:'帮助',
			margin:'0 0 0 2',
			hidden : !isNormalPage,
			listeners:{
				click: function(t){
					var form = t.ownerCt.ownerCt;
					form.showHelpWindow();
				}
			}
		},
		{
			id: 'prev',
			iconCls: 'x-nbutton-icon-left',
			xtype:'button',
			hidden : !hasVoucher,
			listeners:{
				render: function(btn){
					if(parent.Ext) {
						var datalist = parent.Ext.getCmp(datalistId);
						if(datalist){
							var datalistStore = datalist.currentStore;
							Ext.each(datalistStore, function(){
								if(this.selected == true){
									if(this.prev == null){
										btn.disable(true);
									}
								}
							});
						} else {
							btn.disable(true);
						}
					}
				},
				click: function(btn){
					var datalist = parent.Ext.getCmp(datalistId);
					if(datalist){
						var datalistStore = datalist.currentStore;
						var form = Ext.getCmp('form');
						var newId = 0;
						var idx = 0;
						Ext.each(datalistStore, function(s, index){
							if(this.selected == true){
								if(this.prev != null){
									newId = this.prev;
									idx = index;
								}
							}
						});
						datalistStore[idx].selected = false;
						datalistStore[idx-1].selected = true;
						var url = window.location.href;
						if(form.keyField) {
							url = url.replace(/formCondition=(\w*)(IS|=)(\d*)/, 'formCondition=$1$2' + newId);
							url = url.replace(/gridCondition=(\w*)(IS|=)(\d*)/, 'gridCondition=$1$2' + newId);
						}
						window.location.href = url;
					}
				}
			}
		},{
			xtype: 'button',
			iconCls: 'x-nbutton-icon-right',
			id: 'next',
			tooltip: '下一条',
			hidden : !hasVoucher,
			listeners:{
				render: function(btn){
					if(parent.Ext) {
						var datalist = parent.Ext.getCmp(datalistId);
						if(datalist){
							var datalistStore = datalist.currentStore;
							Ext.each(datalistStore, function(){
								if(this.selected == true){
									if(this.next == null){
										btn.disable(true);
									}
								}
							});
						} else {
							btn.disable(true);
						}
					}
				},
				click: function(btn){
					var datalist = parent.Ext.getCmp(datalistId);
					if(datalist){
						var datalistStore = datalist.currentStore;
						var form = Ext.getCmp('form');
						var newId = 0;
						var idx = 0;
						Ext.each(datalistStore, function(s, index){
							if(s.selected == true){
								if(s.next != null){
									newId = s.next;
									idx = index;
								}
							}
						});
						datalistStore[idx].selected = false;
						datalistStore[idx+1].selected = true;
						var url = window.location.href;
						if(form.keyField) {
							url = url.replace(/formCondition=(\w*)(IS|=)(\d*)/, 'formCondition=$1$2' + newId);
							url = url.replace(/gridCondition=(\w*)(IS|=)(\d*)/, 'gridCondition=$1$2' + newId);
						}
						window.location.href = url;
					}
				}
			}
		}];
	},
	ifadmin:function(v){
		var table='EMPLOYEE';
		var field='EM_TYPE';
		Ext.Ajax.request({
			url : basePath + '/common/isadmin.action',
			params: {
				table:table,
				field:field
			},
			method : 'get',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					
					if(r.data!='admin'){
						v.disable();
					}
					
				} 
			}
		});
	},
	getMessageInfo:function(id,caller){
		if(Ext.getCmp('msgwin' + id)){
			Ext.getCmp('msgwin' + id).show();
		} else {
		var me=this;
		Ext.Ajax.request({
			url : basePath + 'common/getMessageInfo.action',
			params: {
				caller:caller,
				id:id
			},
			method : 'get',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					
					me.showmessagelog(r.logs,id);	
				} 
			}
		});
	}
		
	},
	showmessagelog:function(logs,id){
		if(logs.length<1){
			Ext.create('Ext.window.Window', {
			title: '<span style="color:#CD6839;">消息日志</span>',
			iconCls: 'x-button-icon-set',
			closeAction: 'hide',
			height: "90%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'form',			
				ignore: true,
				bodyStyle: 'background:#f1f1f1;',
				autoScroll: true,
				html:'<div style="left:35%;position:absolute;top:30%;font-weight:bold;font-size:25px;color:rgba(144, 143, 143, 0.5)">本单据不存在消息日志</div>'
			}],
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					this.ownerCt.ownerCt.close();
				}
			}]
			}).show();
		}else{
			Ext.create('Ext.window.Window', {
			id : 'msgwin'+id,
			title: '<span style="color:#CD6839;">消息日志</span>',
			iconCls: 'x-button-icon-set',
			closeAction: 'hide',
			height: "90%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'gridpanel',
				ignore: true,
				bodyStyle: 'background:#f1f1f1;',
				autoScroll: true,
				store: Ext.create('Ext.data.Store', {
					fields: ['IH_DATE', 'IHD_RECEIVE', 'IH_CONTEXT', 'IHD_READSTATUS','IHD_READTIME'],
					data: logs
				}),
				columnLines: true,
				columns: [
				          { header: '发出时间', 
				          	dataIndex: 'IH_DATE',
				          	flex: 1.5 , 
				          	renderer: function(val){
				        	  if(val != '无数据'){
				        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
				        	  }
				          }},
				          { header: '接收人', 
				          	dataIndex: 'IHD_RECEIVE', 
				          	flex: 1 ,
				          	renderer: function(val){
				        	  if(val == em_name){
				        		  return '<font color=red>' + val + '</font>';
				        	  } else {
				        		  return val;
				        	  }
				          }},
				          { header: '信息描述', 
				          	dataIndex: 'IH_CONTEXT', 
				          	flex: 3.5
				          },
				          { header: '阅读状态', 
				          	dataIndex: 'IHD_READSTATUS', 
				          	flex: 1,
					        renderer:function(value,meta,record){
							    if(value==0){
							    	return '<font color="#ff0000">未读</font>';
							    }else if(value==-1){
							    	return '<font color="#4795ef">已读</font>'; 
							    }
							     
							}  
						  },
						  { header: '阅读时间', 
						 	dataIndex: 'IHD_READTIME', 
						 	flex: 1.5,
						 	renderer: function(val){
				        	  if(val != ''&&val!=null){
				        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
				        	  }
				          }
						  }]
					}],
			
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					Ext.getCmp('msgwin' + id).close();
				}
			}]
			
			}).show();	
		}
		
	},
	reportset:function(){
		var me=this;
		var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
		forms = Ext.ComponentQuery.query('form'); 
		grids = Ext.ComponentQuery.query('gridpanel');
		formSet = [], gridSet = [];
		if(forms.length > 0) {
			Ext.Array.each(forms, function(f){
				f.fo_id && (formSet.push(f.fo_id));
			});
		}
		if(grids.length > 0) {
			Ext.Array.each(grids, function(g){
				gridSet.push(g.caller || window.caller);
			});
			gridSet = Ext.Array.unique(gridSet);
		}
		if(formSet.length > 0 || gridSet.length > 0) {
			url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
		}
		var myurl=basePath+url;
		Ext.create('Ext.window.Window', {
			title: '<span style="color:#CD6839;">单据设置</span>',
			iconCls: 'x-button-icon-set',
			closeAction: 'hide',
			height: "90%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{	
				xtype:'tabpanel',
				anchor: '100% 100%',
				layout : 'anchor',
				items:[{
					xtype: 'panel',
					title:'界面配置',
					anchor: '100% 100%',	
					items:[
					{	xtype: 'component',
						id:'iframe_detail_multiform',									
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: myurl}
					}]
					
				},{
					title:'逻辑配置',
					xtype: 'panel',
					items:[
					{	xtype: 'component',
						id:'iframe_detail_config',									
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: basePath + 'jsps/ma/logic/config.jsp?whoami=' +caller}
					}]					
				},{
					
					xtype: 'panel',
					title:'知会设置',
					items:[{
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						autoScroll:true,
						html:'<iframe src="' + basePath + 'jsps/sysmng/MsgSetting.jsp?whoami=' +caller+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					}]
				}]
			}],
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					this.ownerCt.ownerCt.close();
				}
			}]
			}).show();
		
	},
	/*操作日志*/
getLogs: function(id){
		if(Ext.getCmp('win' + id)){
			Ext.getCmp('win' + id).show();
		} else {
			Ext.Ajax.request({//拿到grid的columns
				url : basePath + 'common/getMessageLogs.action',
				async: false,
				params: {
					caller: caller,
					id:  id
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var logs = res.logs;
					logs = logs.length == 0 ? [{ml_date: $I18N.common.grid.emptyText, ml_man: $I18N.common.grid.emptyText, 
						ml_content: $I18N.common.grid.emptyText, ml_result: $I18N.common.grid.emptyText}] : logs;
						Ext.create('Ext.window.Window', {
							id : 'win' + id,
							title: '<span style="color:#CD6839;">操作日志</span>',
							iconCls: 'x-button-icon-set',
							closeAction: 'hide',
							height: "90%",
							width: "90%",
							maximizable : true,
							buttonAlign : 'center',
							layout : 'anchor',
							items: [{
								anchor: '100% 100%',
								xtype: 'gridpanel',
								ignore: true,
								bodyStyle: 'background:#f1f1f1;',
								autoScroll: true,
								store: Ext.create('Ext.data.Store', {
									fields: ['ml_date', 'ml_man', 'ml_content', 'ml_result'],
									data: logs
								}),
								columnLines: true,
								columns: [
								          { header: '时间', dataIndex: 'ml_date', flex: 1.5 , renderer: function(val){
								        	  if(val != '无数据'){
								        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
								        	  }
								          }},
								          { header: '操作人员', dataIndex: 'ml_man', flex: 1 ,renderer: function(val){
								        	  if(val == em_name){
								        		  return '<font color=red>' + val + '</font>';
								        	  } else {
								        		  return val;
								        	  }
								          }},
								          { header: '操作', dataIndex: 'ml_content', flex: 1.5},
								          { header: '结果', dataIndex: 'ml_result', flex: 3}
								          ]
							}],
							buttons : [{
								text : '关  闭',
								iconCls: 'x-button-icon-close',
								cls: 'x-btn-gray',
								handler : function(){
									Ext.getCmp('win' + id).close();
								}
							}]
						}).show();
				}
			});
		}
	},
	saveAsExcel:function(id,caller){
		if(id==null || id =='') showMessage('提示','无法导出空数据单据',1000);
		else window.location.href=basePath+'excel/savePanelAsExcel.action?id='+id+"&caller="+caller+"&_noc=1";
	},
	/**
	 * 拿到流程处理情况
	 */
	getProcess: function(id){
		var me = this;
		//先获取jprocess的nodeId
		Ext.Ajax.request({
			url : basePath + 'common/getJProcessByForm.action',
			async: false,
			params: {
				caller: me.realCaller||caller,
				keyValue: id,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.node && localJson.node != -1){
					//再根据nodeId调取流程信息
					if(Ext.getCmp('win-flow' + id)){
						Ext.getCmp('win-flow' + id).show();
					} else {
						var form = Ext.create('Ext.form.Panel', {
							layout: 'column',
							defaultType: 'textfield',
							anchor: '100% 20%' ,
							bodyStyle: 'background:#f1f1f1;',
							fieldDefaults: {
								columnWidth: 0.33,
								readOnly: true,
								cls : "form-field-allowBlank",
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'
							},
							items: [{
								id: 'jp_name',
								name: 'jp_name',
								fieldLabel: '流程名称',
								columnWidth: 0.33
							},{
								columnWidth: 0.33,
								xtype: 'textfield',
								fieldLabel: '发起时间',
								name: 'jp_launchTime',
								id:'jp_launchTime',
								readOnly: true,
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'
							},{
								fieldLabel: '发起人', 
								columnWidth: 0.33,
								xtype: 'textfield',
								id:'jp_launcherName',
								name: 'jp_launcherName',
								readOnly: true,
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'	   				    		
							},{
								fieldLabel: '节点名称', 
								id: 'jp_nodeName',
								name: 'jp_nodeName',
								xtype: 'textfield',
								readOnly: true,   
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'
							},{
								fieldLabel: '处理人',  
								id: 'jp_nodeDealMan',
								name: 'jp_nodeDealMan',
								xtype: 'textfield',
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;',
								readOnly: true,  
								listeners:{
									change:function(field){			
										var em=Ext.getCmp('jp_nodeDealMan').getValue();
										var btn=Ext.getCmp('dealbutton');
										if(em!=em_code) btn.setDisabled(true);
									}
								}
							},{
								fieldLabel: '审批状态',
								id:'jp_status',
								name:'jp_status',
								xtype: 'textfield',
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;',
								readOnly: true
							}],
							loader: {
								url: basePath + 'common/getCurrentNode.action',
								renderer: function(loader, response, active) {
									var res = Ext.decode(response.responseText);
									if(res.info.currentnode.jp_nodeDealMan){
										res.info.currentnode.jp_nodeDealMan=res.info.dealmanname+"("+res.info.currentnode.jp_nodeDealMan+")";
									}else res.info.currentnode.jp_nodeDealMan=res.info.dealmanname+"("+res.info.currentnode.jp_candidate+")";
									res.info.currentnode.jp_launchTime = Ext.Date.format(new Date(res.info.currentnode.jp_launchTime), 'Y-m-d H:i:s');
									this.target.getForm().setValues(res.info.currentnode);
									return true;
								},
								autoLoad: true,
								params: {
									jp_nodeId: localJson.node,
									_noc:1
								}
							},
							buttonAlign: 'center',
							buttons: [{
								text: $I18N.common.button.erpFlowButton,
								iconCls: 'x-button-icon-scan',
								cls: 'x-btn-gray',
								id:'dealbutton',
								handler: function(btn){
									me.FormUtil.onAdd(caller + '_flow', '流程处理', 'jsps/common/jprocessDeal.jsp?formCondition=jp_nodeidIS' + localJson.node);		   
								}
							},{
								text : '关  闭',
								iconCls: 'x-button-icon-close',
								cls: 'x-btn-gray',
								handler : function(){
									Ext.getCmp('win-flow' + id).close();
								}
							}]
						});
						Ext.create('Ext.window.Window', {
							id : 'win-flow' + id,
							title: '<span style="color:#CD6839;">流程处理情况</span>',
							iconCls: 'x-button-icon-set',
							closeAction: 'hide',
							height: "90%",
							width: "90%",
							maximizable : true,
							buttonAlign : 'center',
							layout : 'fit',
							items:[{
								xtype:'tabpanel',
								frame:true,
								layout:'fit',
								items:[{
									title:'处理明细',
									layout:'anchor',
									frame:true,
									items:[form, Ext.create("erp.view.common.JProcess.GridPanel",{
										anchor: '100% 80%' ,
										nodeId: localJson.node
									})]
								},{ 
									title : '节点设置',
									items:[{
										tag : 'iframe',
										style:{
											background:'#f0f0f0',
											border:'none'
										},						  
										frame : true,
										border : false,
										layout : 'fit',
										height:window.innerHeight*0.9,
										iconCls : 'x-tree-icon-tab-tab',
										html : '<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor/workfloweditorscan.jsp?jdId='+localJson.jd+"&type="+localJson.type+"&nodeId="+localJson.node+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
									}]				    			
								},{ 
									title : '流程图',
									items:[{
										tag : 'iframe',
										style:{
											background:'#f0f0f0',
											border:'none'
										},						  
										frame : true,
										border : false,
										layout : 'fit',
										height:window.innerHeight*0.9,
										iconCls : 'x-tree-icon-tab-tab',
										html : '<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor/workfloweditorscan.jsp?jdId='+localJson.jd+"&type="+localJson.type+"&nodeId="+localJson.node+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
									}]				    			
								},{
									title:'历史处理明细',
									layout:'anchor',
									frame:true,
									items:[Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
										anchor: '100% 100%' ,
										nodeId: localJson.node
									})]								
								}]

							}]
						}).show();
					}
				} else {
					showMessage("提示", "当前单据无流程处理!");
				}
			}
		});
	},
	SetNodeDealMan:function(id){
		var me=this;
		var nodewin=Ext.getCmp('win-nodeflow'+id);
		if(nodewin){
			nodewin.show();
		}else {
			Ext.create('Ext.window.Window', {
				id : 'win-nodeflow' + id,
				title: '<span style="color:#CD6839;">单据设置</span>',
				iconCls: 'x-button-icon-set',
				closable: false,
				height: "100%",
				width: "90%",
				maximizable : true,
				buttonAlign : 'center',
				layout : 'fit',
				items:[{
					title:'设置流程节点',
					anchor: '100% 90%' ,
					xtype:'SetNodeGridPanel',
					keyValue: id,
					FlowCaller:caller
				}],
				buttons:['->',{
					text:'保 存',
					iconCls: 'x-button-icon-save',
					cls: 'x-btn-gray',
					width : 65,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){
						var win=btn.ownerCt.ownerCt,grid=win.items.items[0];
						var msg=grid.GridUtil.checkGridDirty(grid);
						if(msg==''){
							Ext.Msg.alert('提示','无任何修改!');
						}else {
							var param= grid.GridUtil.getGridStore(grid);
							param= unescape("[" + param.toString().replace(/\\/g,"%") + "]");						
							me.FormUtil.setLoading(true);
							var url=grid.xtype=='TaskGridPanel'?'plm/task/updatePageTask.action?_noc=1':'common/updateJnodePerson.action?_noc=1';
							Ext.Ajax.request({
								url : basePath+url,			      	   		
								params: {
									param:param,
									caller:caller,
									keyValue:id
								},
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.success){
										showMessage('提示', '保存成功!', 1000);
										grid.loadNewStore(grid,caller,id);
									}else if(localJson.exceptionInfo != null){
										showError(res.exceptionInfo);return;
									}
								}
							});
						}
					}
				},{
					text:'关 闭',
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					width: 65,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){			        	
						btn.ownerCt.ownerCt.hide();
						var form=Ext.getCmp('form');
						var f = form.statuscodeField;
						if(Ext.getCmp(f) && Ext.getCmp(f).value == 'ENTERING'){
							showMessage('提示', '提交成功!', 1000);
							window.location.reload();
						}  	
					}
				},'->']
			}).show();
		}

	},	
	/**
	 * 关联查询
	 */
	showRelativeQuery: function() {
		var me = this,
		win = Ext.getCmp('ext-relative-query');
		if(!win) {
			win = Ext.create('Ext.Window', {
				id: 'ext-relative-query',
				width: '90%',
				height: '90%',
				closeAction: 'hide',
				//title: '<font style="font-family: KaiTi;color:#333;">' + me.title + '</font>--查询',
				title: '<span style="color:#CD6839;">关联查询</span>',
				layout: 'anchor',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe src="' + basePath + 'jsps/common/relativeSearch.jsp?whoami=' + caller + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			});
		}
		win.show();
	},
	showHelpWindow:function(){
		var me = this,
		win = Ext.getCmp('ext-help'),path;
		Ext.Ajax.request({
			url : basePath + 'ma/help/scan.action',
			params: {
				caller:caller
			},
			method : 'get',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					path=r.path;
				} 
			}
		});
		if(!win) {
			win = Ext.create('Ext.Window', {
				id: 'ext-help',
				width: '90%',
				height: '90%',
				closeAction: 'hide',
				title: '帮助文档',
				modal:true,
				layout: 'border',
				items: [{
					region:'center',
					tag : 'iframe',
					layout : 'fit',
					html : '<iframe src="' + basePath + path + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}/*,{
					region: 'south',
					height: 100,
					split: true,
					collapsible: true,
					title: '相关信息',
					minHeight:60,
					collapsed: true,
					html: '相关信息'	
				}*/]
			});
		}
		win.show();
	},
	parseVoucherConfig : function(config) {
		var form = this, keys = Ext.Object.getKeys(config), args = {};
		Ext.each(keys, function(k){
			if (typeof config[k] === 'function') {
				args[k] = config[k].call(null, form);
			} else if (k == 'yearmonth') {
				args[k] = form.getYearmonthByField(config[k]);
			} else if (k == 'datas') {
				args[k] = form.getDataByField(config[k]);
			} else {
				args[k] = config[k];
			}
		});
		return args;
	},
	createVoucher: function(config) {
		var me = this, args = this.parseVoucherConfig(config);
		if('unneed' == args.kind) {
			showMessage('提示', '该类型单据无需制作凭证！');
			return;
		}
		me.getVoucher(function(data){
			var vf = me.voucherConfig.voucherField;
			if (!Ext.isEmpty(data[vf])) {
				if(data[vf] != 'UNNEED') {
					var box = Ext.create('Ext.window.MessageBox', {
						buttonAlign : 'center',
						buttons: [{
							text: '查看凭证',
							handler: function(b) {
								me.linkVoucher(data);
								b.ownerCt.ownerCt.close();
							}
						},{
							text: '取消凭证',
							handler: function(b) {
								me.onVoucherUnCreate(args);
								b.ownerCt.ownerCt.close();
							}
						},{
							text: '关闭',
							handler : function(b) {
								b.ownerCt.ownerCt.close();
							}
						}]
					});
					box.show({
						title : "提示",  
						msg : "当前单据已制作过凭证:" + data[vf],  
						icon : Ext.MessageBox.QUESTION
					});
				} else
					showMessage('提示', '该类型单据无需制作凭证！');
			} else {
				warnMsg('现在制作凭证?', function(b){
					if (b == 'ok' || b == 'yes') {
						me.beforeVoucherCreate(args);
					}
				});
			}
		});
	},
	beforeVoucherCreate : function(args) {
		var me = this;
		if(me.keyField && me.statusField) {
			var val = me.down('#' + me.keyField).getValue(),
			params = {
				caller: me.tablename,
				field: me.voucherConfig.status || me.statusField,
				condition: me.keyField + '=' + val 
			};
			Ext.Ajax.request({
				url : basePath + 'common/getFieldData.action',
				params: params,
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);
					} else if(r.success && (args.statusValue || 'POSTED') == r.data){
						me.onVoucherCreate(args);
					} else {
						showError('单据还未过账!');
					}
				}
			});
		}
	},
	onVoucherCreate : function(args) {
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'fa/vc/createVoucher.action',	
			params: args,
			callback: function(opt, s, r) {
				me.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
    				if(rs.success && rs.content){
		   				var msg = "";
		   				Ext.Array.each(rs.content, function(item){
		   					if(item.errMsg) {
		   						msg += item.errMsg + '<hr>';
		   					} else if(item.id) {
		   						msg += '凭证号:<a href="javascript:openUrl2(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' 
	    							+ item.id + '&gridCondition=vd_voidIS' + item.id + '\',\'凭证\',\'vo_id\','+item.id+');">' + item.code + '</a><hr>';	
		   					}
		   				});
    					showMessage('提示', msg);
		   			}
    			}
			}
		});
	},
	onVoucherUnCreate : function(args) {
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'fa/vc/unCreateVoucher.action',
			params: args,
			callback: function(opt, s, r) {
				me.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.error) {
					showMessage('提示', rs.error);
				} else {
					showMessage('提示', '取消成功!');
					var vf = me.voucherConfig.voucherField, v = me.down('#' + vf);
					v && v.setValue(null);
				}
			}
		});
	},
	getDataByField : function(field) {
		var form = this, f = form.child('#' + field);
		return f ? "'" + f.getValue() + "'" : '';
	},
	getYearmonthByField : function(field) {
		var form = this;
		var f = form.child('#' + field),
		v = f ? (Ext.isDate(f.value) ? f.value : Ext.Date.parse(f.value, 'Y-m-d')) : new Date();
		return Ext.Date.format(v, 'Ym');
	},
	getVoucher : function(callback) {
		var me = this, vf = me.voucherConfig.voucherField;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			params: {
				caller : me.tablename + ' left join voucher on vo_code=' + vf,
				fields : 'vo_id,' + vf,
				condition : me.keyField + '=' + me.down('#' + me.keyField).value
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				}
				if(r.success){
					callback.call(null, r.data || {});
				}
			}
		});
	},
	linkVoucher : function(data) {
		this.FormUtil.onAdd(null, '凭证', 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' +
				data.vo_id + '&gridCondition=vd_voidIS' + data.vo_id);
	},
	/*添加任务*/
	addTask : function() {
		var me=this,win=Ext.getCmp('win_task');
		if(!win){
			win=Ext.create('Ext.window.Window', {
				id : 'win_task',
				title: '单据任务',
				closeAction: 'hide',
				width: '90%',
				height: '100%',
				maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				items: [{
					anchor: '100% 100%',
					xtype: 'TaskPanel',
					sourceForm:me
				}]
			});
		}
		win.show();
	},
	setFormValues : function(data){
		var form = Ext.getCmp('form');
		form.getForm().setValues(data);
		var o = {};
		var status = Ext.getCmp('statuscode_');
		if (status && status.value != 'ENTERING') {
			form.readOnly = true;
			form.getForm().getFields().each(function(field) {  
		        field.setReadOnly(true);    
		    });  
		    form.fireEvent('afterload', form);
		}
	},
	getIdentity : function(){
		// 取唯一值
		var me = this, identity = me.identity, kfield = me.keyField;
		if (identity) {
			return (typeof identity === 'function' ? identity.call(me) : identity);
		} else if (kfield) {
			return Ext.getCmp(kfield).value;
		}
		return null;
	},
	expData : function() {
		window.open(basePath + 'common/dump/exp.action?type=' + caller + "&identity=" + encodeURIComponent(this.getIdentity()));
	}
});