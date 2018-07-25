Ext.define('erp.view.common.sysinit.SysrefreshGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.sysrefreshgrid',
	autoScroll: true,
	manageHeight: false,
	store:Ext.create('Ext.data.Store', {
        fields:['IC_DESCRIPTION','IC_URL'],
	}),
	columns:[{
		header:'刷新项',
		dataIndex:'IC_DESCRIPTION',
		flex:1
	},{
		header:'刷新路径',
		dataIndex:'IC_URL',
		width:0,
	},{
		text: '',
		dataIndex: 'check',
		flex: 0.3,
		renderer: function(val, meta, record) {
			meta.tdCls = val;
			return '';
		}
	},{
        menuDisabled: true,
        sortable: false,
        xtype: 'actioncolumn',
        width: 50,
        items: [{
            iconCls: 'refresh',
            tooltip: '刷新',
            text:'刷新',
            handler: function(grid, rowIndex, colIndex) {
            	console.log(grid);
                grid.ownerCt.refreshItem(grid.ownerCt,rowIndex);
            }
        }]
    }],
    listeners:{
      afterrender:function(grid){
    	 Ext.defer(grid.loadStore(),600);
      }	
    },
	initComponent : function() {
		var me = this;
		this.callParent();
	},
	refreshItem:function(grid,idx){
		console.log('sdsdd');
		var me = this, r;
    	if(Ext.isNumber(idx)) {
    		r = grid.store.getAt(idx);
    	}
    	r.set('check', 'loading');
    	var action=r.get('IC_URL');
    	console.log(action);
    		Ext.Ajax.request({
    			url: basePath + action,
    			async:false,
    			method: 'GET',
    			timeout: 600000,
    			callback: function(opt, s, re) {
    				r.set('check', 'checked');
    				grid.toggleRow(r);
    				var rs = Ext.decode(re.responseText);
    				if(rs.error) {
    					r.set('check', 'error');
    				}
    				if(rs.result) {
    					r.set('detail', rs.result);
    				}
    				if(Ext.isNumber(idx)) {
    					me.check(grid, ++idx, btn);
    				} else {
    					btn.setDisabled(false);
    				}
    			}
        	});
	},
	loadStore:function(){
		var me=this;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			params: {
				fields:'ic_description,ic_url',
				caller:'initcheckitem',
				condition:"ic_model='"+getUrlParam('whoami')+"'"
			},
			method : 'post',
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);
				var data=Ext.decode(res.data);
				me.getStore().loadData(data);
			}
		});
	}
	
});
