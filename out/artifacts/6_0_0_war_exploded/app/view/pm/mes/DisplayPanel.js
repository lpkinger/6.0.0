/**
 * ERP项目gridpanel通用样式2
 */
Ext.define('erp.view.pm.mes.DisplayPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpDisplayGridPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu','erp.view.core.button.ExportDetail'],
	region: 'south',
	layout : 'fit',
	directImport:false,//直接将Excel数据导入从表 false：不支持
	deleteBeforeImport : false,
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true, 
	store: [],
	columns: [],
	binds:null,
	limitArr:[],
	bodyStyle: 'background-color:#f1f1f1;',
	plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	features : [Ext.create('Ext.grid.feature.GroupingSummary',{
		startCollapsed: true,
		groupHeaderTpl: '{name} (共:{rows.length}条)'
	}),{
		ftype : 'summary',
		showSummaryRow : false,//不显示默认合计行
		generateSummaryData: function(){
			// 避开在grid reconfigure后的计算，节约加载时间50~600ms
			return {};
		}
	}],
	bbar: [{
		xtype : 'erpExportDetailButton'
	} ],
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	necessaryField: '',//必填字段
	detno: '',//编号字段
	keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	dbfinds: [],
	caller: null,
	condition: null,
	gridCondition:null,
	initComponent : function(){
		var me=this;
		if(!this.boxready) {
			var condition = this.condition;
			condition= condition == null || condition== "null"  ? '': condition;
			var querycondition= this.querycondition;			
			var formCondition = this.BaseUtil.getUrlParam('formCondition');
			formCondition = formCondition == null || formCondition== "null"  ? '':  formCondition.replace(/IS/g, "=");
			if(formCondition!=''){
				if (condition!= ''){
					condition=condition+' and '+querycondition.replace('@formCondition',formCondition);
				}else{
					condition=querycondition.replace('@formCondition',formCondition);
				}
			}
			var gridParam = {caller: this.caller || caller, condition:condition, _m: 0};
			me.getGridColumnsAndStore(me, 'common/singleGridPanel.action', gridParam);
		} 
		this.callParent(arguments);
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		grid.setLoading(true);
		if(!param._config) param._config=getUrlParam('_config');
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	async: (grid.sync ? false : true),
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		if (!response) return;
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
        				Ext.each(limits,function(l){
        					if(l.lf_field.indexOf(' ') > -1){
        						limitArr.push(l.lf_field.split(' ')[1]);
        					}else{
        						limitArr.push(l.lf_field);
        					}
        				});
    				}
        			var reg =new RegExp("^yncolumn-{1}\\d{0,1}$");
        			Ext.each(res.columns, function(column, y){
        				if(column.xtype=='textareatrigger'){
        					column.xtype='';
        					column.renderer='texttrigger';
        				}
        				//yncoloumn支持配置默认是/否
        				if(column.xtype &&reg.test(column.xtype)&&(column.xtype.substring(8)==-1||column.xtype.substring(8)==-0)){
        					Ext.each(res.fields, function(field, y){
                				if(field.type=='yn' && column.dataIndex==field.name){
                					field.defaultValue=0-column.xtype.substring(9);
                				}
        					});
        					column.xtype='yncolumn';
        				}
        				// column有取别名
        				if(column.dataIndex.indexOf(' ') > -1) {
        					column.dataIndex = column.dataIndex.split(' ')[1];
        				}
        				//power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        					column.hideable= false;
        				}
        				//renderer
        				me.GridUtil.setRenderer(grid, column);
        				//logictype
        				me.GridUtil.setLogicType(grid, column, {
        					headerColor: res.necessaryFieldColor
        				});
        			});
        			//data
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			me.GridUtil.add10EmptyData(grid.detno, data);
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		}
            		//store
            		var store = me.GridUtil.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
            		//view
            		if(grid.selModel && grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		//dbfind
            		if(res.dbfinds && res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
            		//reconfigure
            		if(grid.sync) {//同步加载的Grid
            			grid.reconfigure(store, res.columns);
            			grid.on('afterrender', function(){
            				me.GridUtil.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
            			});
            		} else {
            			//toolbar
            			if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				me.GridUtil.setToolbar(grid, res.columns, grid.necessaryField, limitArr);
            			}else{
            				grid.limitArr=limitArr;
            			}
            			grid.reconfigure(store, res.columns);
            		}
            		if(grid.buffered) {//缓冲数据的Grid
            			grid.verticalScroller = Ext.create('Ext.grid.PagingScroller', {
            				activePrefetch: false,
            				store: store
            			});
            			store.guaranteeRange(0, Math.min(store.pageSize, store.prefetchData.length) - 1);
            		}
            		var vp = grid.up('viewport'), form = (vp ? vp.down('form') : null);
        			if(form){ 
        				grid.readOnly = !!form.readOnly;//grid不可编辑
        				form.on('afterload', function(){
        					grid.readOnly = !!form.readOnly;
        				});
        			}
        		}
        	}
        });
	}
	
});