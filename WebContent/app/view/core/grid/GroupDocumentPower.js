/**
 * ERP项目groupgrid样式:hrjob分组
 */
Ext.define('erp.view.core.grid.GroupDocumentPower',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.groupdocumentpower',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
    	startCollapsed: true,
        groupHeaderTpl: '<input type="checkbox" id="group{name}" onclick="selectOrg(\'{name}\',\'group{name}\')"/>{name} ({rows.length} 人)'
    })],
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name: 'jo_orgid',
        	type: 'number'
        }, {
        	name:'jo_orgname',
        	type:'string'
        },{
        	name:'jo_description',
        	type:'string'
        },{
        	name:'jo_name',
        	type:'string'
        },{
        	name:'jo_emid',
        	type:'number'
        },{
        	name:'jo_id',
        	type:'number'
        },{
        	name:'dpp_add',
        	type:'bool'
        },{
        	name:'dpp_delete',
        	type:'bool'
        },{
        	name:'dpp_upload',
        	type:'bool'
        },{
        	name:'dpp_download',
        	type:'bool'
        },{
        	name:'dpp_update',
        	type:'bool'
        }],
        sorters: [{
            property : 'jo_emid',
            direction: 'ASC'
        }],
        groupField: 'jo_orgname'
    }),
    columns: [{
    	text: '岗位资料',
    	columns: [{
            text: 'ID',
            width: 0,
            dataIndex: 'jo_id'
        },{
            text: '组织ID',
            flex: 1,
            dataIndex: 'jo_orgid'
        },{
            text: '组织名称',
            flex: 1,
            dataIndex: 'jo_orgname'
        },{
            text: '岗位描述',
            flex: 1.5,
            dataIndex: 'jo_description'
        },{
            text: '岗位名称',
            flex: 1,
            dataIndex: 'jo_name'
        },{
            text: '员工ID',
            flex: 1,
            dataIndex: 'jo_emid'
        }]
    },{
    	text: '操作名称',
    	columns: [{
        	text: '添加文件夹',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dpp_add'
        },{
        	text: '删除文件/文件夹',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dpp_delete'
        },{
        	text: '上传',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dpp_upload'
        },{
        	text: '下载',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dpp_download'
        },{
        	text: '修改',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dpp_update'
        }]
    }],
    tbar: [{
    	iconCls: 'tree-save',
		text: $I18N.common.button.erpSaveButton,
		handler: function(){
			var grid = Ext.getCmp('grid');
			var documentpositionpowers = [];
			var documentpositionpower = null;
			Ext.each(grid.store.data.items, function(){
				if(this.dirty){
					documentpositionpower = new Object();
					documentpositionpower.dpp_dcpid = grid.dcp_id;
					documentpositionpower.dpp_id = this.data['dpp_id'];
					documentpositionpower.dpp_joid = this.data['jo_id'];
					documentpositionpower.dpp_add = this.data['dpp_add'] ? 1 : 0;
					documentpositionpower.dpp_delete = this.data['dpp_delete'] ? 1 : 0;
					documentpositionpower.dpp_upload = this.data['dpp_upload'] ? 1 : 0;
					documentpositionpower.dpp_download = this.data['dpp_download'] ? 1 : 0;
					documentpositionpower.dpp_update = this.data['dpp_update'] ? 1 : 0;
					documentpositionpowers.push(documentpositionpower);
				}
			});
			if(documentpositionpowers.length > 0){
				Ext.Ajax.request({//拿到grid的columns
		        	url : basePath + 'hr/employee/updateJobDocumentPower.action',
		        	params: {
		        		update: Ext.encode(documentpositionpowers)
		        	},
		        	method : 'post',
		        	callback : function(options,success,response){
		        		var res = new Ext.decode(response.responseText);
		        		if(res.exception || res.exceptionInfo){
		        			showError(res.exceptionInfo);
		        			return;
		        		}
		        		if(res.success){
		        			updateSuccess(function(){
		        				parent.Ext.getCmp("win").close();
		        			});
		        		}
		        	}
				});
			}
		}
    },'-',{
    	iconCls: 'group-close',
		text: $I18N.common.button.erpCloseButton,
		handler: function(){
			parent.Ext.getCmp("win").close();
		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		this.dcp_id = getUrlParam('dcp_id');
		this.getGroupData();
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getGroupData: function(){
		var me = this;
		//先取HrJob,HrJob的配置是放在datalist表里面的
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'hr/employee/getHrJob_dcp.action',
        	params: {
        		id: me.dcp_id
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
//        		console.log(response);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		if(!res.hrjob){
        			return;
        		} else {
        			var data = new Array();
        			Ext.each(res.hrjob, function(){
        				var j = this;
        				Ext.each(res.documentpositionpower, function(){
        					if(j.jo_id == this.dpp_joid){
        						j = Ext.Object.merge(j, this);
        						j.dpp_id = this.dpp_id;
        						j.dpp_add = this.dpp_add == 1;
        						j.dpp_delete = this.dpp_delete == 1;
        						j.dpp_upload = this.dpp_upload == 1;
        						j.dpp_download = this.dpp_download == 1;
        						j.dpp_update = this.dpp_update == 1;
        					}
        				});
        				data.push(j);
        			});
        			me.store.loadData(data);
        		}
        	}
        });
	},
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		listeners:{
			 'select': function(view, record){
				 this.selectAllPower(record);
			 },
			 'deselect': function(view, record){
				 this.deselectAllPower(record);
			 }
		},
		selectAllPower: function(record){
	    	record.set('dpp_add', true);
	    	record.set('dpp_delete', true);
	    	record.set('dpp_upload', true);
	    	record.set('dpp_download', true);
	    	record.set('dpp_update', true);
	  },
	  deselectAllPower: function(record){
		    record.set('dpp_add', false);
	    	record.set('dpp_delete', false);
	    	record.set('dpp_upload', false);
	    	record.set('dpp_download', false);
	    	record.set('dpp_update', false);
	   }
	})
});
function selectOrg(orgname, id){
	var dom = document.getElementById(id);
	var grid = Ext.getCmp('grid');
	var selected = new Array();
	Ext.each(grid.store.data.items, function(){
		if(this.data['jo_orgname'] == orgname){
			selected.push(this);
		}
	});
	if(dom.checked){
		grid.selModel.select(selected);
	} else {
		grid.selModel.deselect(selected);
	}
}