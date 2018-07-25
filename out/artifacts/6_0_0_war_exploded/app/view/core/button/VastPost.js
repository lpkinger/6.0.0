/**
 * 批量抛转按钮
 */	
Ext.define('erp.view.core.button.VastPost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastPostButton',
		text: $I18N.common.button.erpVastPostButton,
    	tooltip: '可以抛转多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastPostButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90,
		handler: function(){
			var grid = Ext.getCmp('grid');
			var records = grid.getMultiSelected();
			if(records.length > 0){
				var me = this;
		    	var win = new Ext.window.Window({
		    		title: '数据抛转',
			    	id : 'win-post',
					height: "100%",
					width: "80%",
					maximizable : true,
					modal: true,
					buttonAlign : 'center',
					layout : 'anchor',
					items: [me.createForm(), me.createGrid(grid)],
				});
				win.show();
				this.getCurrentMaster();
				this.getMasters();
			}
		},
		/**
		 * 
		 */
		createForm: function(){
			var me = this;
			return Ext.create('Ext.form.Panel', {
			    anchor: '100% 23%',
			    bodyStyle: 'background:#f1f1f1;',
			    layout: 'column',
			    items: [{
			    	xtype: 'fieldcontainer',
			    	columnWidth: 0.3,
			    	layout: 'column',
			    	style: {
			            marginTop: '10px',
			            marginLeft: '10px'
			        },
			    	items: [{
			    		xtype: 'displayfield',
			    		id: 'ma_name',
			    		name: 'ma_name',
			    		columnWidth: 1,
			    		fieldLabel: '当前账套编号'
			    	},{
			    		xtype: 'displayfield',
			    		id: 'ma_function',
			    		name: 'ma_function',
			    		columnWidth: 1,
			    		fieldLabel: '当前账套描述'
			    	}]
			    },{
			    	xtype: 'displayfield',
			    	columnWidth: 0.15,
			    	labelSeparator: '',
			    	fieldLabel: '<img src="' + basePath + 'resource/images/screens/network.png">'
			    },{
			    	xtype: 'displayfield',
			    	columnWidth: 0.1,
			    	labelSeparator: '',
			    	style: {
			            marginTop: '20px',
			        },
			    	fieldLabel: '<img src="' + basePath + 'resource/images/screens/arrow.png">'
			    },{
			    	xtype: 'displayfield',
			    	columnWidth: 0.15,
			    	labelSeparator: '',
			    	fieldLabel: '<img src="' + basePath + 'resource/images/screens/network.png">'
			    },{
			    	xtype: 'fieldcontainer',
			    	columnWidth: 0.3,
			    	layout: 'column',
			    	style: {
			            marginTop: '10px',
			            marginRight: '10px'
			        },
			    	items: [{
			    		xtype: 'combobox',
			    		id: 'ma_name_t',
			    		name: 'ma_name_t',
			    		columnWidth: 1,
			    		fieldLabel: '选择抛转账套',
			    		displayField: 'display',
			    		valueField: 'value',
			    		queryMode: 'local',
			    		editable: false,
			    		store: Ext.create('Ext.data.Store', {
			                fields: ['display', 'value'],
			                data : []
			            })
			    	},{
			    		xtype: 'displayfield',
			    		id: 'ma_function_t',
			    		name: 'ma_function_t',
			    		fieldLabel: '抛转账套描述',
			    		columnWidth: 1
			    	},{
			    		xtype: 'hidden',
			    		id: 'ma_id_t',
			    		name: 'ma_id_t'
			    	}]
			    }],
		    	buttonAlign: 'center',
		    	buttons: [{
		    		text: '开始抛转',
		    		iconCls: 'x-button-icon-download',
		    		cls: 'x-btn-gray',
		    		handler: function(btn){
		    			var maid = Ext.getCmp('ma_id_t').value;
		    			if(Ext.isEmpty(maid)){
		    				alert("请先选择账套!");
		    			} else {
		    				me.post(btn.up('window').down('grid'), maid);
		    			}
		    		}
		    	},{
		    		text: '取消',
		    		iconCls: 'x-button-icon-close',
		    		cls: 'x-btn-gray',
		    		handler: function(){
		    			Ext.getCmp('win-post').close();
		    		}
		    	}]
			});
		},
		/**
		 * 
		 */
		createGrid: function(grid){
			var records = grid.getMultiSelected();
			var fields = new Array();
			Ext.each(Ext.Object.getKeys(records[0].data), function(f){
				fields.push('_' + f);
			});
			fields.push('_status');
			fields.push('_error');
			var cols = new Array();
			cols.push({
				dataIndex: '_status',
				text: '<font color=blue>抛转状态</font>',
				width: 100,
				cls: 'x-grid-header-1',
				renderer: function(val){
					if(val == '未抛转'){
						return '<font color=blue>未抛转</font>';
					} else if(val == '抛转中'){
						return '<img src="' + basePath + 'resource/images/download.png">' + '<font color=blue>...</font>';
					} else if(val == '抛转成功'){
						return '<img src="' + basePath + 'resource/images/face/1.gif">' + '<font color=blue>抛转成功</font>';
					} else if(val == '抛转失败'){
						return '<img src="' + basePath + 'resource/images/face/6.gif">' + '<font color=red>抛转失败</font>';
					} else {
						return val;
					}
				}
			});
			cols.push({
				dataIndex: '_error',
				text: '<font color=blue>失败原因</font>',
				width: 150,
				cls: 'x-grid-header-1',
				hidden: true
			});
			Ext.each(grid.columns, function(c){
				if(c.text != '&#160;'){
					cols.push({
						dataIndex: '_' + c.dataIndex,
						text: c.text,
						width: c.width,
						hidden: c.hidden,
						xtype: c.xtype,
						cls: 'x-grid-header-1'
					});
				}
			});
			var datas = new Array();
			Ext.each(records, function(r){
				var d = r.data;
				Ext.each(fields, function(f){
					if(f == '_status'){
						d._status = '未抛转';
					} else {
						d[f] = d[f.substr(1)];
					}
				});
				datas.push(d);
			});
			return Ext.create('Ext.grid.Panel', {
				anchor: '100% 77%',
				bodyStyle: 'background:#f1f1f1;',
				columns: cols,
				columnLines: true,
				store: Ext.create('Ext.data.Store', {
					fields: fields,
					data: datas
				})
			});
		},
		/**
		 * 加载当前用户所在账套
		 */
		getCurrentMaster: function(){
			Ext.Ajax.request({
				url: basePath + 'common/getMasterByEm.action',
				method: 'get',
				callback: function(options,success,response){
					var res = Ext.decode(response.responseText);
					if(res.master){
						Ext.getCmp('ma_name').setValue(res.master.ma_name);
						Ext.getCmp('ma_function').setValue(res.master.ma_function);
					}
				}
			});
		},
		/**
		 * 加载系统所有账套
		 */
		getMasters: function(){
			Ext.Ajax.request({
				url: basePath + 'common/getAbleMasters.action',
				method: 'get',
				callback: function(options,success,response){
					var res = Ext.decode(response.responseText);
					if(res.masters){
						var data = new Array();
						Ext.each(res.masters, function(m){
							data.push({
								display: m.ma_name,
								value: m.ma_name,
								desc: m.ma_function,
								id: m.ma_id
							});
						});
						Ext.getCmp('ma_name_t').store.loadData(data);
						Ext.getCmp('ma_name_t').on('change', function(f, n, o, obj){
							Ext.each(data, function(d){
								if(d.value == n){
									Ext.getCmp('ma_function_t').setValue(d.desc);
									Ext.getCmp('ma_id_t').setValue(d.id);
								}
							});
						});
					}
				}
			});
		},
		/**
		 * 抛转数据
		 */
		post: function(grid, maid){
			var me = this;
			var ids = new Array();
			Ext.each(grid.store.data.items, function(){
				ids.push(this.data['_' + keyField]);
				this.set('_status', '抛转中');
				this.commit();
			});
			parent.Ext.getCmp('content-panel').getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + (me.url || 'common/vastPost.action'),
		   		params: {
		   			id: ids,
		   			ma_id: maid
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			parent.Ext.getCmp('content-panel').getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.log){
	    				var bool = false;
	    				Ext.each(localJson.log, function(log){
	    					log = Ext.decode(log);
	    					Ext.each(grid.store.data.items, function(){
	    						if(this.data['_' + keyField] == log.id){
	    							if(log.success){
	    								this.set('_status', '抛转成功');
	    							} else {
	    								bool = true;
	    								this.set('_status', '抛转失败');
	    								this.set('_error', log.error);
	    							}
	    							this.commit();
	    						}
	    					});
	    					if(bool){
	    						Ext.each(grid.columns, function(cn){
		    						if(cn.dataIndex == '_error'){
		    							cn.show();
		    						}
		    					});
	    					}
	    				});
		   			}
		   		}
			});
		}
	});