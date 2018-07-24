/**
 * ERP项目groupgrid样式:hrjob分组
 */
Ext.define('erp.view.core.grid.GroupDocumentListPower',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.groupDocumentListPower',
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
        	name:'dlp_see',
        	type:'bool'
        },{
        	name:'dlp_delete',
        	type:'bool'
        },{
        	name:'dlp_edit',
        	type:'bool'
        },{
        	name:'dlp_download',
        	type:'bool'
        },{
        	name:'dlp_journal',
        	type:'bool'
        },{
        	name:'dlp_share',
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
        	text: '查看文档',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dlp_see'
        },{
        	text: '删除文档',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dlp_delete'
        },{
        	text: '编辑文档',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dlp_edit'
        },{
        	text: '下载',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dlp_download'
        },{
        	text: '查看日志',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dlp_journal'
        },{
        	text: '共享',
        	flex: 0.2,
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkbox',
                cls: 'x-grid-checkheader-editor'
            },
            dataIndex: 'dlp_share'
        }]
    }],
    tbar: [{
    	iconCls: 'tree-save',
		text: $I18N.common.button.erpSaveButton,
		handler: function(){
			var grid = Ext.getCmp('grid');
			var documentlistpowers = [];
			var documentlistpower = null;
			Ext.each(grid.store.data.items, function(){
				if(this.dirty){
					documentlistpower = new Object();
					documentlistpower.dlp_dclid = grid.dcl_id;
					documentlistpower.dlp_id = this.data['dlp_id'];
					documentlistpower.dlp_joid = this.data['jo_id'];
					documentlistpower.dlp_see = this.data['dlp_see'] ? 1 : 0;
					documentlistpower.dlp_delete = this.data['dlp_delete'] ? 1 : 0;
					documentlistpower.dlp_edit = this.data['dlp_edit'] ? 1 : 0;
					documentlistpower.dlp_download = this.data['dlp_download'] ? 1 : 0;
					documentlistpower.dlp_share = this.data['dlp_share'] ? 1 : 0;
					documentlistpower.dlp_journal = this.data['dlp_journal'] ? 1 : 0;
					documentlistpowers.push(documentlistpower);
				}
			});
			if(documentlistpowers.length > 0){
				Ext.Ajax.request({//拿到grid的columns
		        	url : basePath + 'hr/employee/updateJobDocumentListPower.action',
		        	params: {
		        		update: Ext.encode(documentlistpowers)
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
		this.dcl_id = getUrlParam('dcl_id');
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
        	url : basePath + 'hr/employee/getHrJob_dcl.action',
        	params: {
        		id: me.dcl_id
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
        				Ext.each(res.documentlistpower, function(){
        					if(j.jo_id == this.dlp_joid){
        						j = Ext.Object.merge(j, this);
        						j.dlp_id = this.dlp_id;
        						j.dlp_see = this.dlp_see == 1;
        						j.dlp_delete = this.dlp_delete == 1;
        						j.dlp_share = this.dlp_share == 1;
        						j.dlp_download = this.dlp_download == 1;
        						j.dlp_edit = this.dlp_edit == 1;
        						j.dlp_journal = this.dlp_journal == 1;
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
	    	record.set('dlp_see', true);
	    	record.set('dlp_delete', true);
	    	record.set('dlp_share', true);
	    	record.set('dlp_download', true);
	    	record.set('dlp_edit', true);
	    	record.set('dlp_journal', true);
	  },
	  deselectAllPower: function(record){
		  record.set('dlp_see', false);
	    	record.set('dlp_delete', false);
	    	record.set('dlp_share', false);
	    	record.set('dlp_download', false);
	    	record.set('dlp_edit', false);
	    	record.set('dlp_journal', false);
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