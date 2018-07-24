/**
 * 应收对账全部差异
 */
Ext.define('erp.view.fa.fix.ShowDifferAll',{ 
	extend: 'Ext.Viewport', 
	alias: 'widget.showdifferall',
	layout: 'anchor', 
	hideBorders: true, 
	id: 'showdifferall', 
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){
		Ext.data.Store.grouperIdFn = function(grouper) {
            return grouper.id || grouper.property;
        };
        Ext.data.Store.groupIdFn = function(group) {
            return group.key;
        };
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id:'grid',
				anchor: '100% 100%',
				columnLines: true,
				columns: me.defaultColumns,
				store: me.store,
				dockedItems : [{
			    	xtype: 'toolbar',
			    	dock: 'top',
			    	margin:'0 0 5 0',
			    	style:{background:'#fff'},
			    	items: [{
						name: 'export',
						text: $I18N.common.button.erpExportButton,
						iconCls: 'x-button-icon-excel',
				    	cls: 'x-btn-gray'
					},'->',{
						text: $I18N.common.button.erpCloseButton,
						iconCls: 'x-button-icon-close',
						id:'close',
				    	cls: 'x-btn-gray',
				    	margin: '0 4 0 0'
					}]
			    }],
			    plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				features : [ Ext.create('erp.view.core.feature.FloatingGrouping', {
					groupHeaderTpl : '{name} (共:{rows.length}条)'
				}) ]
			}]
		});
		me.callParent(arguments);
    },
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'tb_catecode',
        	type: 'string'
        },{
        	name: 'tb_catename',
        	type: 'string'
        },{
        	name: 'tb_currency',
        	type: 'string'
        },{
        	name: 'tb_kind',
        	type: 'string'
        },{
        	name: 'tb_code',
        	type: 'string'
        },{
        	name: 'tb_vouchercode',
        	type: 'string'
        },{
        	name: 'tb_vonumber',
        	type: 'string'
        },{
        	name: 'tb_asamount',
        	type: 'string'
        },{
        	name: 'tb_debitorcredit',
        	type: 'string'
        },{
        	name: 'tb_glamount',
        	type: 'string'
        },{
        	name: 'tb_balance',
        	type: 'string'
        },{
        	name: 'tb_index',
        	type: 'number'
        },{
        	name: 'tb_void',
        	type:'number'
        },{
        	name: 'tb_catetype',
        	type:'string'
        }],
        data: [],
        groupers: [{
        	property: 'tb_catetype',
        	transform: function(value) {
        		switch(value){
        		case '固定资产':
        			return 1;
        		case '累计折旧':
        			return 2;
        		}
        	}
        }],
        groupField:'tb_catetype'
    }),

    defaultColumns: [{
		dataIndex:'tb_catetype',
		text: '类型',
		hidden:true
	},{
		dataIndex: 'tb_catecode',
		cls: 'x-grid-header-1',
		text: '科目编号',
		width: 120
	},{
		dataIndex: 'tb_catename',
		cls: 'x-grid-header-1',
		text: '科目描述',
		width: 250
	},{
		dataIndex: 'tb_currency',
		cls: 'x-grid-header-1',
		text: '币别',
		width: 0
	},{
		dataIndex: 'tb_kind',
		cls: 'x-grid-header-1',
		text: '单据类型',
		width: 130,
		renderer: function(val, meta, record) {
			if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(record.get('tb_kind')) > -1) {
				meta.style = 'font-weight: 700';
			}
			return val;
		}
	},{
		dataIndex: 'tb_code',
		xtype: 'linkcolumn',
		cls: 'x-grid-header-1',
		text: '单据编号',
		width: 130,
		align: 'center',
		renderer: function(val, meta, record) {
			if (['合并制作', '小计', '无'].indexOf(val) > -1) {
				return val;
			}
			return this.defaultRenderer(val, meta, record);
			
		},
		handler: function(view, cell, recordIndex, cellIndex, config, e, record, row) {
			var kind = record.get('tb_kind');
			if(!this.LinkUtil){
				this.LinkUtil = Ext.create('erp.util.LinkUtil');
			}
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: 'voucherbill',
		   			field: 'vb_vscode',
		   			condition: 'vb_void=' + record.get('tb_void')
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else if(r.success && r.data){
		   				if (['AccountRegiste','BillAP','BillARChange','BillAR','BillAPChange'].indexOf(r.data) > -1) {
		   					kind = r.data;
		   				}
		   			}
		   		}
			});
			this.openBillPage(this.LinkUtil.getLinkByKind(kind), record);
		},
		openBillPage : function(cfg, record) {
			if (!cfg) {
				return;
			}
			var me = this, k = cfg.keyfield, m = cfg.mainfield, code = record.get('tb_code'),
				condition = (cfg.codefield + '=\'' + code + '\'' + (cfg.kindfield ? (' and ' + cfg.kindfield + '=\'' + cfg.kind + '\'') : ''));
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: cfg.table,
		   			field: k,
		   			condition: condition
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else if(r.success && r.data){
		   				cfg.url += cfg.url.indexOf('?') > 0 ? '&' : '?';
		   				openUrl2(cfg.url + 'formCondition=' + k + 'IS' + r.data + '&gridCondition=' + m + 'IS' + r.data, cfg.kind + '(' + code + ')');
		   			}
		   		}
			});
		}
	},{
		dataIndex: 'tb_asamount',
		cls: 'x-grid-header-1',
		text: '固定资产系统金额',
		width: 150,
		align:'right',
		xtype:'numbercolumn',
		format: '0,000.00',
		renderer: function(val, meta, record) {
			if(val != 0){
				return val;
			}
		}
	},{
		dataIndex: 'tb_vonumber',
		xtype: 'linkcolumn',
		cls: 'x-grid-header-1',
		text: '凭证号',
		width: 80,
		align: 'center',
		linkUrl: 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS{tb_void}&gridCondition=vd_voidIS{tb_void}',
		linkTabTitle: '凭证({tb_vouchercode})'
	},{
		dataIndex: 'tb_debitorcredit',
		cls: 'x-grid-header-1',
		text: '方向',
		width: 0
	},{
		dataIndex: 'tb_glamount',
		cls: 'x-grid-header-1',
		text: '总账系统金额',
		width: 150,
		align:'right',
		xtype:'numbercolumn',
		format: '0,000.00',
		renderer: function(val, meta, record) {
			if(val != 0){
				return val;
			}
		}
	},{
		dataIndex: 'tb_balance',
		cls: 'x-grid-header-1',
		text: '差额',
		width: 150,
		align:'right',
		format: '0,000.00',
		xtype:'numbercolumn',
		renderer: function(val, meta, record) {
			if(val!=0){
				if (['期初余额', '期末余额', '本期借方发生', '本期贷方发生'].indexOf(record.get('tb_kind')) > -1) {
					meta.style = 'font-weight: 700';
				}
				return Ext.util.Format.number(val, '0,000.00');
			}
		}
	}]
});