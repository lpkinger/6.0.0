Ext.QuickTips.init();
Ext.define('erp.controller.fa.CheckAccountSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.CheckAccountSet','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.TextAreaTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'gridpanel[id=mainset]': { 
    			afterrender:function(grid){
    				me.getCheckItems(grid);
    			},
    			beforeedit: function (editor, e, eOpts) {
    				var ed = editor.column.getEditor(editor.record);	
    				var index = editor.grid.store.indexOf(editor.record);					
    				if(editor.value&&ed.xtype=='textareatrigger'){
    					return false;
    				}
    			},
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record, true);
    			}
    		},
    		'gridpanel[id=paramset]': { 
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record, false);
    			}
    		},
    		'gridpanel[id=errorset]': { 
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record, false);
    			}
    		},
    		'button[id=deleteSet]': {
    			click:function(btn){
    				var grid = btn.ownerCt.ownerCt,key,str;
    				var checkcode = grid.checkcode,isParamSet,record = grid.selModel.getLastSelected();
    				if(grid.id=='paramset'){
    					isParamSet = true;
    					str ='参数设置项';
    					if(record.dirty){
    						key=record.modified['key_'];
    					}else{
    						key=record.data['key_'];
    					}
    				}else{
    					isParamSet = false;
    					str = '错误输出设置项';
    					if(record.dirty){
    						key=record.modified['field_'];
    					}else{
    						key=record.data['field_'];
    					}
    				}
    				Ext.Msg.confirm('提示','确定删除'+str+(record.data['key_']?record.data['key_']:record.data['field_'])+'?',function(option){
						if(option=='yes'){
							Ext.Ajax.request({
								url:basePath + 'fa/deleteSet.action',
								params:{
									checkcode: checkcode,
									key: key,
									isParamSet: isParamSet
								},
								method:'post',
								callback:function(options,success,resp){
									var res = new Ext.decode(resp.responseText);
									if(res.success){
										grid.store.remove(record);				
									}
									if(res.exceptionInfo){
										showError(res.exceptionInfo);
										return;
									}
								}
							});
						}
					});
    			}
    		},
    		'#mainset actioncolumn[dataIndex=enable_]':{
    			checkchange:function(column,recordIndex, checked){
    				var record = column.ownerCt.ownerCt.store.getAt(recordIndex);
    				var billoutmode = record.data['billoutmode_'];
    				if(billoutmode!="all"){
    					var module = Ext.getCmp('module').value;
    					Ext.Ajax.request({
							url:basePath + 'fa/getCheckItemStatus.action',
							params:{
								module: module,
								billoutmode: billoutmode,
								checked: checked
							},
							method:'post',
							callback:function(options,success,resp){
								var res = new Ext.decode(resp.responseText);
								if(res.exceptionInfo){
									record.set(column.dataIndex, !checked);
									showError(res.exceptionInfo);						
								}
							}
						});
    				}
        			
    			}
    		},
    		'field[name=module]': {
    			change:function(field, newValue, oldValue, eOpts){
    				if(!field.unchange){
	    				var grid = Ext.getCmp('mainset');
						var CheckItems = me.getGridStore(grid,true);
						var bool = true;
						if(CheckItems.length>0){
							Ext.Msg.confirm('提示','当前界面内容已发生变化，切换模块前要先保存吗？',function(option){
								if(option=='yes'){
									bool = me.saveCheckItems(CheckItems,grid);
				    				if(!bool){
				    					field.unchange = true;
				    					field.setValue(oldValue);
				    				}
								}else{
									me.getCheckItems(grid);
								}
							});
						}else{
							me.getCheckItems(grid);
						}
    				}
    				field.unchange = false;
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpCloseButton': {
    			afterrender: function(btn){
    				var main = parent.Ext.getCmp("content-panel");
    				if(main){
	    				main.getActiveTab().on('beforeclose',function(panel){
    						var grid = Ext.getCmp('mainset'),CheckItems=new Array(),bool = true;
    						if(!grid.contine){
								CheckItems = me.getGridStore(grid,true);
							}	
							if(CheckItems.length>0){
								Ext.Msg.confirm('提示','当前界面内容已发生变化，关闭界面前要先保存吗？',function(option){
									if(option=='yes'){
										bool = me.saveCheckItems(CheckItems,grid,true);
										if(bool){
											grid.contine = true;
											panel.close();
										}
									}else{
										grid.contine = true;
										panel.close();
									}
								});
								return false;
							}
    					});
    				}
    			}
    		},
    		'actioncolumn#set':{
    			paramset:function(gridView,select){
    				me.clickAction(gridView,select,true);
    			},
    			errorset:function(gridView,select){
    				me.clickAction(gridView,select,false);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record, isMainSet){//grid行选择
		var me = this;
		var grid = selModel.ownerCt;
		if(grid && !grid.readOnly && !grid.NoAdd){
			var index = grid.store.indexOf(record);
			if(index == grid.store.indexOf(grid.store.last())){
				if(isMainSet){
					var module = Ext.getCmp('module').value;
					var detno = parseInt(record.data['detno_']);
					me.addEmptyData(grid.store,module,detno+1);//就再加10行
				}else{
					me.add10EmptyData(grid.store);
				}
			}
			var btn = grid.down('button[id=deleteSet]');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('copydetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('pastedetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('updetail');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('downdetail');
				if(btn)
					btn.setDisabled(false);
				if(grid.down('tbtext[name=row]')){
					grid.down('tbtext[name=row]').setText(index+1);
				}
	    }
	},
    getCheckItems:function(grid){
    	var me = this;
    	var module = Ext.getCmp('module').value;
    	
		Ext.Ajax.request({
			url:basePath + 'fa/getCheckItems.action',
			params:{
				module:module
			},
			method:'post',
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.data.length<1){
						grid.store.removeAll();
						me.addEmptyData(grid.store,module);
					}else{
						grid.store.loadData(res.data);
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});
    },
    addEmptyData: function(store,module,detno){
    	var datas = new Array();
    	if(!detno){
    		detno = 1;
    	}
    	for(var i=0;i<10;i++){
			var o = new Object();
			o.enable_=null;
			o.code_=null;
			o.module_=module;
			o.detno_=detno+i;
			o.title_=null;
			o.execute_=null;
			o.billoutmode_='all';
			o.man_=null;
			o.date_=null;
			datas.push(o);
		}
		store.loadData(datas,true);
   },
   clickAction:function(grid,select,isParamSet){
   		var me = this;
   		var bool = true;
   		var checkcode = select.data['code_'];
   		var str = '';
   		if(isParamSet){
   			str='检测项保存之后才能进行参数设置，需要先保存当前检测项吗？';
   		}else{
   			str='检测项保存之后才能进行错误输出设置，需要先保存当前检测项吗？';
   		}
   		if(!checkcode||checkcode==''){
   			bool = false;
	   		Ext.Msg.confirm("提示", str, function(optional) {
				if (optional == 'yes') {
					var title = select.data['title_'];
					if(!title||title==''){
						Ext.Msg.alert('警告','检测项描述为空，不能保存检测项！');
						return false;
					}
					var CheckItems = me.getGridStore(grid,false);
					for(var i=0;i<CheckItems.length;i++){
						if(CheckItems[i].detno_==select.data.detno_){
							showError('序号重复！');
							return;
						}
						if(CheckItems[i].title_==select.data.title_){
							showError('与序号'+CheckItems[i].detno_+"的检测项描述重复！");
							return;
						}
					}
					Ext.Ajax.request({
						url:basePath + 'fa/saveCheckItem.action',
						params:{
							CheckItem: Ext.JSON.encode(select.data)
						},
						method:'post',
						async: false,
						callback:function(options,success,resp){
							var res = new Ext.decode(resp.responseText);
							if(res.success){
								bool = true;
								if(res.data){
									select.set(res.data);
									select.commit();
								}
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);						
							}
						}
					});
				}
			});
   		}
   		if(bool){
   			var forms= new Object();
			forms.detno=select.data['detno_'];
			forms.title=select.data['title_'];
			forms.billoutmode=select.data['billoutmode_'];
			forms.execute=select.data['execute_'];
			this.createWindow(forms, checkcode,isParamSet);
   		}
		
   },
   createWindow: function(forms, checkcode,isParamSet) {
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : isParamSet?'参数设置':'错误输出设置',
			closeAction: 'destroy',
			modal : true,
			width : '55%',
			height: '80%',
			layout: 'anchor',
			items : [{
				xtype: 'form',
				anchor: '100% 15%',
				frame : true,
				layout: 'column',
				autoScroll : true,
				defaultType : 'textfield',
				labelSeparator : ':',
				buttonAlign : 'center',
				cls: 'u-form-default',
				fieldDefaults : {
					fieldStyle : 'background:#FFFAFA;color:#515151;',
					focusCls: 'x-form-field-cir-focus',
					labelAlign : 'left',
					msgTarget: 'side',
					blankText : $I18N.common.form.blankText
				},
				items: [{	
					fieldLabel:'序号',
					columnWidth:0.5,
					readOnly:true,
					name:'detno_',
					fieldStyle:'background:#e0e0e0;',
					value:forms.detno
				},{	
					fieldLabel:'检测项描述',
					xtype:'textareatrigger',
					columnWidth:0.5,
					editable:false,
					name:'title_',
					value:forms.title
				},{	
					fieldLabel:'开票模式',
					columnWidth:0.5,
					readOnly:true,
					fieldStyle:'background:#e0e0e0;',
					name:'billoutmode_',
					xtype:'combo',
					store:{
					    fields: ['display', 'value'],
					    data : [
					        {display:'全部', value:'all'},
					        {display:'开票记录模式', value:'useBillOut'},
					        {display:'非开票记录模式', value:'notBillOut'}
					    ]
					},
					queryMode: 'local',
    				displayField: 'display',
    				valueField: 'value',
    				value:forms.billoutmode
				},{	
					fieldLabel:'取数SQL',
					xtype:'textareatrigger',
					columnWidth:0.5,
					editable:false,
					name:'execute_',
					value:forms.execute
				}]
			},me.getSetGrid(checkcode,isParamSet)],
			buttonAlign : 'center',
			buttons : [{
				text : $I18N.common.button.erpConfirmButton,
				height : 26,
				handler : function(b) {
					var grid = b.ownerCt.ownerCt.down('gridpanel');
					me.saveSets(grid,false);
				}
			}, {
				text : $I18N.common.button.erpCloseButton,
				height : 26,
				handler : function(b) {
					var w = b.ownerCt.ownerCt;
					w.close();
				}
			}],
			listeners:{
				beforeclose:function(window,eOpts){
					var grid = window.down('gridpanel'),bool = true,data=new Array();
					if(!grid.contine){
						data = me.getGridStore(grid,true);
					}					
					if(data.length>0){
						Ext.Msg.confirm("提示", '当前界面内容已发生变化，关闭界面前要先保存吗？', function(optional) {
							if (optional == 'yes') {
								bool = me.saveSets(grid,true);
								if(bool){
									grid.contine = true;	
									window.close();
								}
							}else{
								grid.contine = true;
								window.close();
							}
						});
						return false;
					}else{
						return true;
					}
				}
			}
		});
		win.show();
	},
	getSetGrid: function(checkcode,isParamSet){
		var me = this;
		var columns = new Array();
		var url = '';
		var store = {};
		var id = '';
		if(isParamSet){
			id='paramset';
			url='fa/getParamSets.action';
			store = Ext.create('Ext.data.Store',{
				fields:['key_','getkeySql_','data_type'],
				data:[]
			});
			columns=[
				{
					header:'变量名',
					dataIndex:'key_',
					cls : 'x-grid-header-1',
					width:120,
					editor:{
						xtype:'textfield'
					},
					style:'color:#FF0000',
					necessField:true
				},{
					header:'获取SQL',
					dataIndex:'getkeySql_',
					cls : 'x-grid-header-1',
					width:500,
					editor:{
						xtype:'textareatrigger'
					}
				},{
					header:'数据类型',
					xtype:'combocolumn',
					dataIndex:'data_type',
					cls : 'x-grid-header-1',
					defaultValue:'STRING',
					width:100,
					editor:{
						xtype:'combo',
						store:{
						    fields: ['display', 'value'],
						    data : [
						        {display:'数字', value:'NUMBER'},
						        {display:'字符串', value:'STRING'},
						        {display:'日期', value:'DATE'}
						    ]
						},
						queryMode: 'local',
	    				displayField: 'display',
	    				valueField: 'value'
					}
				}];
		}else{
			id='errorset';
			url='fa/getErrorSets.action';
			store = Ext.create('Ext.data.Store',{
				fields:['field_','desc_','width_','type_','render_'],
				data:[]
			});
			columns=[
				{
					header:'字段名',
					dataIndex:'field_',
					cls : 'x-grid-header-1',
					style:'color:#FF0000',
					necessField:true,
					width:120,
					editor:{
						xtype:'textfield'
					}
				},{
					header:'列描述',
					dataIndex:'desc_',
					cls : 'x-grid-header-1',
					style:'color:#FF0000',
					necessField:true,
					width:120,
					editor:{
						xtype:'textfield'
					}
				},{
					header: '列宽',
					xtype: 'numbercolumn',
					dataIndex: 'width_',
					cls : 'x-grid-header-1',
					width: 120,
					defaultValue: '120',
					modify: false,
					editor: {
						xtype:'numberfield',
						minValue: 0,						
						hideTrigger:true
					}
				},{
					header:'字段类型',
					xtype:'combocolumn',
					dataIndex:'type_',
					cls : 'x-grid-header-1',
					width:120,
					defaultValue:'S',
					editor:{
						xtype:'combo',
						store:{
						    fields: ['display', 'value'],
						    data : [{display: "是否", value: "yncolumn"},									
									{display: "字符串", value: "S"},
									{display: "数字", value: "N"},
									{display: "隐藏域", value: "H"},
									{display: "日期", value: "D"},
									{display: "数字2", value: "F2"},
									{display: "数字3", value: "F3"},
									{display: "数字4", value: "F4"},
									{display: "数字6", value: "F6"},
									{display: "时间", value: "DT"}]
						},
						queryMode: 'local',
	    				displayField: 'display',
	    				valueField: 'value'
					}
				},{
					header:'显示方式',
					dataIndex:'render_',
					cls : 'x-grid-header-1',
					width:350,
					editor:{
						xtype:'textfield'
					}
				}];
		}
		me.getSetItems(checkcode,store,url);
		var grid = Ext.create('Ext.grid.Panel',{
			anchor: '100% 85%',
			layout : 'fit',
			id:id,
			checkcode:checkcode,
			plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		        clicksToEdit : 1
		    })],
		    viewConfig: {  
		      	plugins: {  
		          ptype: 'gridviewdragdrop',  
		          ddGroup:  'DragDropGroup',  
		          enableDrag : true,
		          enableDrop : true
		      	}
	      	},  
		    bbar: [{
				xtype : 'tbtext',
				name : 'row'
			},{
				xtype : 'button',
				id: 'deleteSet',
				iconCls: 'x-button-icon-close',
	    		cls: 'x-btn-tb',
	    		tooltip: $I18N.common.button.erpDeleteDetailButton,
	    		disabled: true
			}, {
				xtype : 'copydetail'
			}, {
				xtype : 'pastedetail'
			}, {
				xtype : 'updetail'
			}, {
				xtype : 'downdetail'
			}],
			autoScroll : true,
		    columnLines : true,
		    store:store,
			columns:columns
		});
		return grid;
	},
	add10EmptyData: function(store){
		var data = new Array();
		for(var i=0;i<10;i++){
			var o = new Object();
			data.push(o);
		}
		store.loadData(data,true);
	},
	beforeUpdate:function(){
		var me = this;
		var grid = Ext.getCmp('mainset');
		var CheckItems = me.getGridStore(grid,true);
		if(CheckItems.length<1){
			Ext.Msg.alert('警告','未修改数据！');
			return;
		}
		me.saveCheckItems(CheckItems,grid);
	},
	saveCheckItems:function(datas,grid,isClose){
		var me = this;
		var CheckItems = me.getGridStore(grid,false),bool=false;
		for(var i=0;i<CheckItems.length-1;i++){
			for(var j=i+1;j<CheckItems.length;j++){
				if(CheckItems[i].detno_==CheckItems[j].detno_){
					showError('序号'+CheckItems[i].detno_+'重复！');
					return false;
				}
				
				if(CheckItems[i].title_.replace(/(^\s*)|(\s*$)/g,'')==CheckItems[j].title_.replace(/(^\s*)|(\s*$)/g,'')&&CheckItems[i].billoutmode_==CheckItems[j].billoutmode_){
					showError('序号'+CheckItems[i].detno_+'与序号'+CheckItems[j].detno_+"的检测项描述重复！");
					return false;
				}
			}
		}
		Ext.Ajax.request({
			url:basePath + 'fa/saveCheckItems.action',
			params:{
				CheckItems: Ext.JSON.encode(datas)
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					showMessage('保存检测项','保存成功',3000);
					if(!isClose)
						me.getCheckItems(grid);
					bool = true;
				}else if(res.exceptionInfo){
					bool = false;
					showError(res.exceptionInfo);	
				}
			}
		});
		return bool;
	},
	getGridStore: function(grid,onlyDirty){
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			var bool = true,jsonGridData = new Array();
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(s[i].dirty ||!onlyDirty){
					Ext.each(grid.columns, function(c){
						if(c.necessField&&Ext.isEmpty(data[c.dataIndex])){
							bool = false;
							return false;
						}
						if(c.xtype == 'datecolumn'){
							c.format = c.format || 'Y-m-d';
							if(Ext.isDate(data[c.dataIndex])){
								dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
							} 
						} else if(c.xtype == 'datetimecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
							}
						} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
								dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							} else {
								dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
							}
						} else if(c.dataIndex){
							dd[c.dataIndex] = s[i].data[c.dataIndex];
						}
						if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0'||dd[c.dataIndex] == '')) {
							dd[c.dataIndex] = c.defaultValue;
						}
						
					});
					if(bool){
						jsonGridData.push(dd);
					}else{
						bool = true;
					}
				}
			}
		}
		return jsonGridData;
	},
	saveSets:function(grid,isClose){
		var me = this;
		var datas = me.getGridStore(grid,false),bool=false;
		var url ='',getUrl='',params=new Object(),checkcode=grid.checkcode;
		if(grid.id=='paramset'){
			for(var i=0;i<datas.length-1;i++){
				for(var j=i+1;j<datas.length;j++){
					if(datas[i].key_==datas[j].key_){
						showError('变量名'+datas[i].key_+'重复！');
						return false;
					}
				}
			}
			url='fa/saveParamSets.action';
			getUrl='fa/getParamSets.action';
			params.checkcode = checkcode;
			params.ParamSets = Ext.JSON.encode(datas);
		}else{
			for(var i=0;i<datas.length-1;i++){
				for(var j=i+1;j<datas.length;j++){
					if(datas[i].field_==datas[j].field_){
						showError('字段名'+datas[i].field_+'重复！');
						return false;
					}
				}
			}
			url='fa/saveErrorSets.action';
			getUrl='fa/getErrorSets.action';
			params.checkcode = checkcode;
			params.ErrorSets = Ext.JSON.encode(datas);
		}
		Ext.Ajax.request({
			url:basePath + url,
			params:params,
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					bool = true;
					showMessage('保存设置','保存成功',3000);
					if(!isClose){
						me.getSetItems(checkcode,grid.store,getUrl);
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});
		return bool;
	},
	getSetItems:function(checkcode,store,url){
		var me = this;
		Ext.Ajax.request({
			url:basePath + url,
			params:{
				checkcode: checkcode
			},
			method:'get',
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.data.length<1){
						me.add10EmptyData(store);
					}else{
						store.loadData(res.data);
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});
	}
});