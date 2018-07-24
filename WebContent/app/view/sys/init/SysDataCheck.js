Ext.define('erp.view.sys.init.SysDataCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					id:'sysDataCheckPanel',
					xtype:'panel',
//					height:80,
					anchor:'100% 10%',
					bodyStyle: 'background:#F0F0F0;',
					border:0,
					layout:'absolute',
					buttonAlign :'center',
					items:[
					       {
						    xtype:'button',
						    text:'组织架构',
						    id:'organazition',
						    width:100,
						    height:30,
						    x:350,
						    y:25
						   },
						  {
						    xtype:'button',
						    text:'流程',
						    id:'process',
						    width:100,
						    height:30,
						    x:450,
						    y:25
						  },
					       {
					    	xtype:'button',
					    	text:'财务',
					    	id:'finance',
					    	bodyStyle: 'background:#F0F0F0;',
					    	width:100,
					    	height:30,
					    	x:550,
					    	y:25
					       },						  
						  {
						    xtype:'button',
						    text:'视图',
						    id:'view',
						    width:100,
						    height:30,
						    x:650,
						    y:25
						  },
						  {
						    xtype:'button',
						    text:'自动执行JOB',
						    id:'JOB',
						    width:100,
						    height:30,
						    x:750,
						    y:25
						  }						  
				]
			},
			{
				xtype:'panel',
				layout:'card',
				id:'cardpanel',
				anchor:'100% 90%',
				items:[
				       	{
				       		xtype: 'grid',
				       		id: 'check-grid',
				       		border: false,
				       		/*style: {
				       		    borderColor: 'red',
				       		    borderStyle: 'solid'
				       		},*/
				       		title:null,
				       		anchor: '100% 100%',
				       		tbar: [{
				       			cls: 'x-btn-gray',
				       			id: 'check',
				       			text: '全部刷新',
				       			width: 80,
				       			margin: '0'
				       		},'->',{
				       			cls: 'x-btn-gray',
				       			id: 'close',
				       			text: $I18N.common.button.erpCloseButton,
				       			width: 80,
				       			margin: '0'
				       		}],
				       		columns: [{
				       			text: '刷新项',
				       			dataIndex: 'value',
				       			flex: 10
				       		},{
				       			text: '',
				       			dataIndex: 'check',
				       			flex: 1,
				       			renderer: function(val, meta, record) {
				       				meta.tdCls = val;
				       				return '';
				       			}
				       		},{
				       			text: '',
				       			dataIndex: 'link',
				       			flex: 1,
				       			renderer: function(val, meta, record, x, y, store) {
				       				var idx = store.indexOf(record);
				       				meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
				       				return '<a href="javascript:Ext.getCmp(\'check-grid\').check(' + idx + ')">优化</a>';
				       			}
				       		}],
				       		columnLines: true,
				       		store: Ext.create('Ext.data.Store',{
				       			fields: [{name: 'action', type: 'string'}, {name: 'value', type: 'string'}, {name: 'detail', type: 'string'}],
				       			data: [{
				       				action: 'hr/refreshOrgLevel.action',
				       				value: '刷新组织层级'
				       			},{
				       				action: 'hr/refreshOrgEmployees.action',
				       				value: '刷新组织人员对照关系'
				       			},{
				       				action: 'hr/refreshOrgJobEmployeeTree.action#hr/employee/getHrOrgsTreeAndEmployees.action',
				       				value: '刷新组织架构树'
				       			},/*{
				       				action: 'common/vastDeployProcess.action',
				       				value: '流程批量保存——批量保存已启用且包含task节点的流程,请不要重复点击'
				       			},{
				       				action: 'common/vastRefreshJnode.action',
				       				value: '流程节点处理人批量刷新'
				       			},*/{
				       				action: 'hr/employee/vastRefreshPower.action',
				       				value: '刷新岗位权限、个人权限'
				       			},{
				       				action: 'hr/refreshJobLevel.action',
				       				value: '刷新岗位层级'
				       			}]
				       		}),
				       		plugins: [{
				       			ptype: 'rowexpander',
				       			rowBodyTpl : [
				       			              '<ul>',          
				       			              '<li style="margin-left:30px;color:gray;">{detail}</li>',
				       			              '</ul>'
				       			              ],
								onToggle: function() {
				       				var grid = this.getCmp();
				       					grid.componentLayout.childrenChanged = true;
										grid.doComponentLayout();
				       			}				       			              
				       		}],
				       		selModel: new Ext.selection.CellModel(),
				       		toggleRow: function(record) {
				       			var rp = this.plugins[0];
				       			if(rp)
				       				rp.toggleRow(this.store.indexOf(record));
				       		}
				       	},
				    	{
				       		xtype: 'grid',
				       		id: 'check-grid1',
				       		border: false,
				       		/*style: {
				       		    borderColor: 'red',
				       		    borderStyle: 'solid'
				       		},*/
				       		title:null,
				       		anchor: '100% 100%',
				       		tbar: [{
				       			cls: 'x-btn-gray',
				       			id: 'check1',
				       			text: '全部刷新',
				       			width: 80,
				       			margin: '0'
				       		},'->',{
				       			cls: 'x-btn-gray',
				       			id: 'close1',
				       			text: $I18N.common.button.erpCloseButton,
				       			width: 80,
				       			margin: '0'
				       		}],
				       		columns: [{
				       			text: '刷新项',
				       			dataIndex: 'value',
				       			flex: 10
				       		},{
				       			text: '',
				       			dataIndex: 'check',
				       			flex: 1,
				       			renderer: function(val, meta, record) {
				       				meta.tdCls = val;
				       				return '';
				       			}
				       		},{
				       			text: '',
				       			dataIndex: 'link',
				       			flex: 1,
				       			renderer: function(val, meta, record, x, y, store) {
				       				var idx = store.indexOf(record);
				       				meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
				       				return '<a href="javascript:Ext.getCmp(\'check-grid1\').check(' + idx + ')">优化</a>';
				       			}
				       		}],
				       		columnLines: true,
				       		store: Ext.create('Ext.data.Store',{
				       			fields: [{name: 'action', type: 'string'}, {name: 'value', type: 'string'}, {name: 'detail', type: 'string'}],
				       			data: [/*{
				       				action: 'hr/refreshOrgLevel.action',
				       				value: '刷新组织层级'
				       			},{
				       				action: 'hr/refreshOrgEmployees.action',
				       				value: '刷新组织人员对照关系'
				       			},{
				       				action: 'hr/refreshOrgJobEmployeeTree.action#hr/employee/getHrOrgsTreeAndEmployees.action',
				       				value: '刷新组织架构树'
				       			},*/{
				       				action: 'common/vastDeployProcess.action',
				       				value: '流程批量保存——批量保存已启用且包含task节点的流程,请不要重复点击'
				       			},{
				       				action: 'common/vastRefreshJnode.action',
				       				value: '流程节点处理人批量刷新'
				       			}/*,{
				       				action: 'hr/employee/vastRefreshPower.action',
				       				value: '刷新岗位权限、个人权限'
				       			}*/]
				       		}),
				       		plugins: [{
				       			ptype: 'rowexpander',
				       			rowBodyTpl : [
				       			              '<ul>',          
				       			              '<li style="margin-left:30px;color:gray;">{detail}</li>',
				       			              '</ul>'
				       			              ],
								onToggle: function() {
				       				var grid = this.getCmp();
				       					grid.componentLayout.childrenChanged = true;
										grid.doComponentLayout();
				       			}				       			              
				       		}],
				       		selModel: new Ext.selection.CellModel(),
				       		toggleRow: function(record) {
				       			var rp = this.plugins[0];
				       			if(rp)
				       				rp.toggleRow(this.store.indexOf(record));
				       		}
				       	},
				    	{
				       		xtype: 'grid',
				       		id: 'check-grid2',
				       		border: false,
				       		/*style: {
				       		    borderColor: 'red',
				       		    borderStyle: 'solid'
				       		},*/
				       		title:null,
				       		anchor: '100% 100%',
				       		tbar: [{
				       			cls: 'x-btn-blue',
				       			id: 'check2',
				       			text: '全部刷新',
				       			width: 80
				       		},'->',{
				       			cls: 'x-btn-blue',
				       			id: 'close2',
				       			text: $I18N.common.button.erpCloseButton,
				       			width: 80
				       		}],
				       		columns: [{
				       			text: '刷新项',
				       			dataIndex: 'value',
				       			flex: 10
				       		},{
				       			text: '',
				       			dataIndex: 'check',
				       			flex: 1,
				       			renderer: function(val, meta, record) {
				       				meta.tdCls = val;
				       				return '';
				       			}
				       		},{
				       			text: '',
				       			dataIndex: 'link',
				       			flex: 1,
				       			renderer: function(val, meta, record, x, y, store) {
				       				var idx = store.indexOf(record);
				       				meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
				       				return '<a href="javascript:Ext.getCmp(\'check-grid2\').check(' + idx + ')">优化</a>';
				       			}
				       		}],
				       		columnLines: true,
				       		store: Ext.create('Ext.data.Store',{
				       			fields: [{name: 'action', type: 'string'}, {name: 'value', type: 'string'}, {name: 'detail', type: 'string'}],
				       			data: [/*{
				       				action: 'hr/refreshOrgLevel.action',
				       				value: '刷新组织层级'
				       			},{
				       				action: 'hr/refreshOrgEmployees.action',
				       				value: '刷新组织人员对照关系'
				       			},{
				       				action: 'hr/refreshOrgJobEmployeeTree.action#hr/employee/getHrOrgsTreeAndEmployees.action',
				       				value: '刷新组织架构树'
				       			},*/{
				       				action: 'common/GL/refreshLedger.action',
				       				value: '总账开帐'
				       			},{
				       				action: 'common/GL/refreshAR.action',
				       				value: '应收确认开帐'
				       			},{
				       				action: 'common/GL/refreshAP.action',
				       				value: '应付确认开帐'
				       			}
				       			/*,{
				       				action: 'hr/employee/vastRefreshPower.action',
				       				value: '刷新岗位权限、个人权限'
				       			}*/]
				       		}),
				       		plugins: [{
				       			ptype: 'rowexpander',
				       			rowBodyTpl : [
				       			              '<ul>',          
				       			              '<li style="margin-left:30px;color:gray;">{detail}</li>',
				       			              '</ul>'
				       			              ],
								onToggle: function() {
				       				var grid = this.getCmp();
				       					grid.componentLayout.childrenChanged = true;
										grid.doComponentLayout();
				       			}				       			              
				       		}],
				       		selModel: new Ext.selection.CellModel(),
				       		toggleRow: function(record) {
				       			var rp = this.plugins[0];
				       			if(rp)
				       				rp.toggleRow(this.store.indexOf(record));
				       		}
				       	},
				    	{
				       		xtype: 'grid',
				       		id: 'check-grid3',
				       		border: false,
				       		/*style: {
				       		    borderColor: 'red',
				       		    borderStyle: 'solid'
				       		},*/
				       		title:null,
				       		anchor: '100% 100%',
				       		tbar: [{
				       			cls: 'x-btn-blue',
				       			id: 'check3',
				       			text: '全部刷新',
				       			width: 80
				       		},'->',{
				       			cls: 'x-btn-blue',
				       			id: 'close3',
				       			text: $I18N.common.button.erpCloseButton,
				       			width: 80
				       		}],
				       		columns: [{
				       			text: '刷新项',
				       			dataIndex: 'value',
				       			flex: 10
				       		},{
				       			text: '',
				       			dataIndex: 'check',
				       			flex: 1,
				       			renderer: function(val, meta, record) {
				       				meta.tdCls = val;
				       				return '';
				       			}
				       		},{
				       			text: '',
				       			dataIndex: 'link',
				       			flex: 1,
				       			renderer: function(val, meta, record, x, y, store) {
				       				var idx = store.indexOf(record);
				       				meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
				       				return '<a href="javascript:Ext.getCmp(\'check-grid3\').check(' + idx + ')">优化</a>';
				       			}
				       		}],
				       		columnLines: true,
				       		store: Ext.create('Ext.data.Store',{
				       			fields: [{name: 'action', type: 'string'}, {name: 'value', type: 'string'}, {name: 'detail', type: 'string'}],
				       			data: [
				       			{
				       				action: 'common/GL/refreshJprocessview.action',
				       				value: 'JPROCESSVIEW 集团子帐套审批流视图'
				       			},{
				       				action:'common/GL/refreshOamessagehistoryview.action',
				       				value:'OA_MESSAGEHISTORY_VIEW 集团子帐套消息提醒视图'
				       			}]
				       		}),
				       		plugins: [{
				       			ptype: 'rowexpander',
				       			rowBodyTpl : [
				       			              '<ul>',          
				       			              '<li style="margin-left:30px;color:gray;">{detail}</li>',
				       			              '</ul>'
				       			              ],
								onToggle: function() {
				       				var grid = this.getCmp();
				       					grid.componentLayout.childrenChanged = true;
										grid.doComponentLayout();
				       			}				       			              
				       		}],
				       		selModel: new Ext.selection.CellModel(),
				       		toggleRow: function(record) {
				       			var rp = this.plugins[0];
				       			if(rp)
				       				rp.toggleRow(this.store.indexOf(record));
				       		}
				       	},
				    	{
				       		xtype: 'grid',
				       		id: 'check-grid4',
				       		border: false,
				       		/*style: {
				       		    borderColor: 'red',
				       		    borderStyle: 'solid'
				       		},*/
				       		title:null,
				       		autoScroll:true,
				       		tbar: [{
				       			cls: 'x-btn-blue',
				       			id: 'check4',
				       			text: '刷新本页',
				       			width: 80
				       		},'->',{
				       			cls: 'x-btn-blue',
				       			id: 'close4',
				       			text: $I18N.common.button.erpCloseButton,
				       			width: 80
				       		}],
				       		columns: [{
				       			text: '刷新项',
				       			dataIndex: 'VALUE',
				       			flex: 10
				       		},{
				       			text: '',
				       			dataIndex: 'check',
				       			flex: 1,
				       			renderer: function(val, meta, record) {
				       				meta.tdCls = val;
				       				return '';
				       			}
				       		},{
				       			text: '',
				       			dataIndex: 'link',
				       			flex: 1,
				       			renderer: function(val, meta, record, x, y, store) {
				       				var idx = store.indexOf(record);
				       				meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
				       				return '<a href="javascript:Ext.getCmp(\'check-grid4\').jobcheck(' + idx + ')">优化</a>';
				       			}
				       		}],
				       		columnLines: true,
				       		store: Ext.create('Ext.data.Store',{
				       			storeId : 'jobStore',
				       			fields: [{name: 'ID', type: 'int'},{name: 'ACTION', type: 'string'}, {name: 'VALUE', type: 'string'}],
				       			pageSize : 15,
 							    proxy:{
									type:'ajax',
						//			async:false, 
									url:basePath + 'pm/mps/getOracleJob.action',
									reader:{
										type : 'json',
										root : 'data',
										totalProperty : 'totalCount'
									},
									writer:{
										type:'json'
									}
								},
								autoLoad:true  		
				       		}),
				       		plugins: [{
				       			ptype: 'rowexpander',
				       			rowBodyTpl : [
				       			              '<ul>',          
				       			              '<li style="margin-left:30px;color:gray;">{detail}</li>',
				       			              '</ul>'
				       			              ],
								onToggle: function() {
				       				var grid = this.getCmp();
				       					grid.componentLayout.childrenChanged = true;
										grid.doComponentLayout();
				       			}				       			              
				       		}],
							dockedItems:[{
								xtype:'pagingtoolbar',
								id:'pagetool',
								store:Ext.data.StoreManager.lookup('jobStore'),
								dock:'bottom',
								emptyMsg:'没有数据',
								displayInfo:true
							}],	
							
				       		selModel: new Ext.selection.CellModel(),
				       		toggleRow: function(record) {
				       			var rp = this.plugins[0];
				       			if(rp)
				       				rp.toggleRow(this.store.indexOf(record));
				       		}
				       	}				       	
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});