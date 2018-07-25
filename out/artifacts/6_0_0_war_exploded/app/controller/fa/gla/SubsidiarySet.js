Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.SubsidiarySet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.SubsidiarySet','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.TextAreaTrigger',
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
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record, true);
    			}
    		},
    		'gridpanel[id=paramset]': { 
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record, false);
    			}
    		},
    		'button[id=deleteSet]': {
    			click:function(btn){
    				var grid = btn.ownerCt.ownerCt,key;
    				var checkcode = grid.checkcode,isParamSet,record = grid.selModel.getLastSelected();
    				isParamSet = true;
					if(record.dirty){
						key=record.modified['shr_name'];
					}else{
						key=record.data['shr_name'];
					}
    				Ext.Msg.confirm('提示','确定删除股东'+record.data['shr_name']+'?',function(option){
						if(option=='yes'){
							Ext.Ajax.request({
								url:basePath + 'fa/gla/deleteShareholdersRateSet.action', //fa/deleteSet.action
								params:{
									checkcode: checkcode,
									key: key
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
    				me.clickAction(gridView,select);
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
					var detno = parseInt(record.data['ss_detno']);
					me.addEmptyData(grid.store,detno+1);//就再加10行
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
		Ext.Ajax.request({
			url:basePath + 'fa/gla/getSubsidiarySet.action', 
			method:'post',
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.data.length<1){
						grid.store.removeAll();
						me.addEmptyData(grid.store);
					}else{
						grid.store.loadData(res.data);
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});
    },
    addEmptyData: function(store,detno){
    	var datas = new Array();
    	if(!detno){
    		detno = 1;
    	}
    	for(var i=0;i<10;i++){
			var o = new Object();
			o.ss_id=0;
			o.ss_enable=null;
			o.ss_mastercode=null;
			o.ss_detno=detno+i;
			o.ss_mastername=null;
			o.man_=null;
			o.date_=null;
			datas.push(o);
		}
		store.loadData(datas,true);
   },
   clickAction:function(grid,select){
   		var me = this;
   		var bool = true;
   		var checkcode = select.data['ss_id'];
   		var str = '账套名称保存之后才能进行股东设置，需要先保存吗？';
   		if(!checkcode||checkcode==''){
   			bool = false;
	   		Ext.Msg.confirm("提示", str, function(optional) {
				if (optional == 'yes') {
					var title = select.data['ss_mastername'];
					if(!title||title==''){
						Ext.Msg.alert('警告','账套名称为空，不能保存！');
						return false;
					}
					title = select.data['ss_name'];
					if(!title||title==''){
						Ext.Msg.alert('警告','公司名称为空，不能保存！');
						return false;
					}
					var CheckItems = me.getGridStore(grid,false);
					for(var i=0;i<CheckItems.length;i++){
						if(CheckItems[i].ss_detno==select.data.ss_detno){
							showError('序号重复！');
							return;
						}
						if(CheckItems[i].ss_mastername==select.data.ss_mastername){
							showError('与序号'+CheckItems[i].ss_detno+"的账套名称重复！");
							return;
						}
					}
					Ext.Ajax.request({
						url:basePath + 'fa/gla/saveSubsidiarySetItem.action', //fa/saveCheckItem.action
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
			forms.detno=select.data['ss_detno'];
			forms.title=select.data['ss_mastername'];
			this.createWindow(forms, checkcode);
   		}
		
   },
   createWindow: function(forms, checkcode) {
		var me = this;
		var win = Ext.create('Ext.window.Window', {
			title : '股东信息设置',
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
					name:'ss_detno',
					fieldStyle:'background:#e0e0e0;',
					value:forms.detno
				},{	
					fieldLabel:'账套名称',
					columnWidth:0.5,
					readOnly:true,
					name:'ss_mastername',
					fieldStyle:'background:#e0e0e0;',
					value:forms.title
				}]
			},me.getSetGrid(checkcode)],
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
	getSetGrid: function(checkcode){
		var me = this;
		var columns = [{
			header:'股东名称',
			dataIndex:'SHR_NAME',
			cls : 'x-grid-header-1',
			width:220,
			dbfind:'SubsidiarySet|ss_mastercode',
			editor:{
				  xtype:'dbfindtrigger',
				  hideTrigger: false,
				  name:'SHR_NAME',
				  which:'grid',
				  dbfind:'SubsidiarySet|ss_mastercode',
				  listeners: {
	                  aftertrigger: function(t, d) {
	                	  var record = Ext.getCmp('paramset').selModel.lastSelected;  				
					       record.set('SHR_NAME', d.data.ss_mastername);
					       record.set('SHR_CODE', d.data.ss_mastercode);
					       record.set('SHR_PID', d.data.ss_id);
	                  }
				  }
			},
			style:'color:#FF0000',
			necessField:true
		},{
			header:'股东编号',
			dataIndex:'SHR_CODE',
			cls : 'x-grid-header-1',
			width:150
		},{
			header:'股东ID',
			dataIndex:'SHR_PID',
			cls : 'x-grid-header-1',
			width:0
		},{
			header:'股东占比%',
			dataIndex:'SHR_RATE',
			cls : 'x-grid-header-1',
			width:100,
			editor:{
				  xtype:'numberfield',
				  format:'0',
				  hideTrigger: true
			}
		}];
		var url = 'fa/gla/getShareholdersRateSet.action'; //fa/getParamSets.action
		var store = Ext.create('Ext.data.Store',{
			fields:['SHR_NAME','SHR_CODE','SHR_RATE','SHR_PID'],
			data:[]
		});
		me.getSetItems(checkcode,store,url);
		var grid = Ext.create('Ext.grid.Panel',{
			anchor: '100% 85%',
			layout : 'fit',
			id: 'paramset',
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
				if(CheckItems[i].ss_detno==CheckItems[j].ss_detno){
					showError('序号'+CheckItems[i].ss_detno+'重复！');
					return false;
				}
				
				if(CheckItems[i].ss_mastername.replace(/(^\s*)|(\s*$)/g,'')==CheckItems[j].ss_mastername.replace(/(^\s*)|(\s*$)/g,'')){
					showError('序号'+CheckItems[i].ss_detno+'与序号'+CheckItems[j].ss_detno+"的账套名称重复！");
					return false;
				}
			}
		}
		Ext.Ajax.request({
			url:basePath + 'fa/gla/saveSubsidiarySet.action', 
			params:{
				CheckItems: Ext.JSON.encode(datas)
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					showMessage('保存','保存成功',3000);
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
		var url ='fa/gla/saveShareholdersRateSet.action'; 
			getUrl='fa/gla/getShareholdersRateSet.action';
			params=new Object(),
			checkcode=grid.checkcode;
			params.checkcode = checkcode;
			params.ParamSets = Ext.JSON.encode(datas);
		for(var i=0;i<datas.length-1;i++){
			for(var j=i+1;j<datas.length;j++){
				if(datas[i].shr_name==datas[j].shr_name){
					showError('股东账套名'+datas[i].shr_name+'重复！');
					return false;
				}
			}
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