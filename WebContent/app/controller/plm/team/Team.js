Ext.QuickTips.init();
Ext.define('erp.controller.plm.team.Team', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.team.Team','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.CopyAll'
    	],
    init:function(){
    var me=this;
    	this.control({ 
    	'erpGridPanel2': {
    		  itemclick: this.onGridItemClick
    		 },
    		'erpSaveButton': {
    			click: function(btn){
					var grids = Ext.ComponentQuery.query('gridpanel');
		         if(grids.length > 0){
		   					var s = grids[0].getStore().data.items;
		   					for(var i=0;i<s.length;i++){
		   					  var rowdata=s[i].data;
		   					  if(rowdata.tm_employeecode!=''&&rowdata.tm_prjid!=''){		   					      
		   					      s[i].set('tm_prjid',Ext.getCmp('team_prjid').value);
		   					      s[i].set('tm_name',Ext.getCmp('team_name').value);
		   					  }
		   					 }
		    				}		
    				this.save(this);
    			}
    		},
    		'dbfindtrigger': {
    			change: function(trigger){
    				if(trigger.name == 'team_prjid'){
    					this.changeGrid(trigger);
    				}
    			}
    		},
    		
    		'textfield[name=team_name]': {
    			change: function(field){
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.store.data.items, function(item){
    					item.set('tm_name',field.value);
    				});
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    			var grids = Ext.ComponentQuery.query('gridpanel');
		         if(grids.length > 0){
		   					var s = grids[0].getStore().data.items;
		   					for(var i=0;i<s.length;i++){
		   					  var rowdata=s[i].data;
		   					  if(rowdata.tm_employeecode!=''&&rowdata.tm_prjid!=''){		   					      
		   					      s[i].set('tm_prjid',Ext.getCmp('team_prjid').value);
		   					      s[i].set('tm_name',Ext.getCmp('team_name').value);
		   					  }
		   					 }
		    				}		
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('team_id').value);
    			}
    		},
    		'erpCopyButton':{
    			click:function(btn){
      			  Ext.create('Ext.window.Window', {
  					title: '复制',
  					height: 200,
  					width: 400,
  					layout: 'column',
  					id:'win',
  					buttonAlign:'center',
  					defaults:{
  						fieldStyle:'background:#FFFAFA;color:#515151;',
  						columnWidth:1
  					},
  					allowDrag:false,
  					items: [{
  						xtype:'dbfindtrigger',
  					    fieldLabel:'项目编号',
  					    name:'prjcode',
  					    id:'prjcode',
  					    fieldStyle:'background:#fffac0;color:#515151;',
  					    allowBlank:false
  					},{
  						xtype:'hidden',
  					    fieldLabel:'项目ID',
  					    name:'prjid',
  					    id:'prjid',
  					    fieldStyle:'background:#fffac0;color:#515151;',
  					    allowBlank:true
  					},{
  					    xtype:'textfield',
  					    fieldLabel:'项目名称',
  					    name:'prjname',
  					    id:'prjname',
  					    allowBlank:true,
  					    fieldStyle : 'background:#E6E6E6',
  					    readOnly:true
  					 }],
  					 buttons:[{
  						 text:$I18N.common.button.erpConfirmButton,	 
  						 xtype:'button',
  						 formBind: true,
  						 handler:function(btn){
  							 var id=Ext.getCmp('prjid').getValue();
  							 if(id==null || id=="0" || id==""){
  								 showError("请选择有效的项目编号！");
  								 return;
  							 }
  							 var code=Ext.getCmp('prjcode').getValue();
  							/* if(code==null || code==""){
  								 showError("请选择要复制的项目编号！");
  								 return;
  							 }*/
  							/* var name=Ext.getCmp('prjname').getValue();
  							 if(code==null||code=="" && name==null||name=="" && id==null||id==""){
  								 return;
  							 }*/
  							 var form=Ext.getCmp('form');
  							 var r=form.getValues();
  			    			  var keys = Ext.Object.getKeys(r), f;
  			    				var reg = /[!@#$%^&*()'":,\/?]/;
  			    				Ext.each(keys, function(k){
  			    					f = form.down('#' + k);
  			    					if(f && f.logic == 'ignore') {
  			    						delete r[k];
  			    					}
  			    					//codeField值强制大写,自动过滤特殊字符
  			    					if(k == form.codeField && !Ext.isEmpty(r[k])) {
  			    						r[k] = r[k].trim().toUpperCase().replace(reg, '');
  			    					}
  			    					if(contains(k, 'ext-', true)){
  			    						delete r[k];
  			    					}
  			    				}); 			    				
  			    				var	grid = Ext.getCmp('grid');
  			    				var	jsonGridData = new Array();
  			    				var form = Ext.getCmp('form');
  			    				var s = grid.getStore().data.items;//获取store里面的数据
  			    				var dd;
  			    				for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
  			    					var data = s[i].data;
  			    					dd = new Object();
  			    						Ext.each(grid.columns, function(c){
  			    							if((!c.isCheckerHd)&&(c.logic != 'ignore')){//只需显示，无需后台操作的字段，自动略去
  			    								
  			    								if(c.xtype == 'datecolumn'){
  			    									c.format = c.format || 'Y-m-d';
  			    									if(Ext.isDate(data[c.dataIndex])){
  			    										dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
  			    									} else {
  			    										if(c.editor){
  			    											dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
  			    										}
  			    									}
  			    								} else if(c.xtype == 'datetimecolumn'){
  			    									if(Ext.isDate(data[c.dataIndex])){
  			    										dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
  			    									} else {
  			    										if(c.editor){
  			    											dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
  			    										}
  			    									}
  			    								} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
  			    									if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
  			    										dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
  			    									} else {
  			    										dd[c.dataIndex] = s[i].data[c.dataIndex];
  			    									}
  			    								} else {
  			    									dd[c.dataIndex] = s[i].data[c.dataIndex];
  			    								}
  			    							}
  			    						});
  			    						if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
  			    							dd[grid.mainField] = Ext.getCmp(form.keyField).value;
  			    						}
  			    						jsonGridData.push(Ext.JSON.encode(dd));
  			    				}
  			    				var params=new Object();
  			    				params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
  			    				params.param = unescape(jsonGridData.toString().replace(/\\/g,"%"));
  			    				params.code=code;
  			    				params.id=id;
  			    				Ext.Ajax.request({
  			    		        	url : basePath +'plm/team/copyTeam.action',
  			    		        	params:params,
  			    		        	method : 'post',
  			    		        	callback : function(options,success,response){
  			    		        		var res = new Ext.decode(response.responseText);
  			    		        		if(res.exceptionInfo != null){
  			    		        			showError(res.exceptionInfo);return;
  			    		        		}else {
  			    		        			Ext.Msg.alert('提示','赋值失败');
  			    		        		}
  			    		        	}
  			    		        }); 
  			    				//btn.ownerCt.ownerCt.close();
  						 }
  					 },{
  						 text:$I18N.common.button.erpCancelButton,
  						 handler:function(){
  							 Ext.getCmp('win').close();
  						 }
  					 }]
  				}).show();
  		  
    			}
    		},
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTeam', '创建团队', 'jsps/plm/team/team.jsp');
    			}
    		},
    	});
    },
     onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('team_code').value == null || Ext.getCmp('team_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	changeGrid: function(trigger){
		var grid = Ext.getCmp('grid');
		Ext.Array.each(grid.store.data.items, function(item){
			item.set('tm_prjid',trigger.value);
		});
	}
	
});