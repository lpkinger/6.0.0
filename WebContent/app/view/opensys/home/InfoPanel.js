Ext.define('erp.view.opensys.home.InfoPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.infopanel',
    id:'infopanel',
    title:'消息中心',
    region: 'center',
    border: false,
   /* split: true,*/
    flex: 2,
	autoScroll: true,
    layout:'fit',
    initComponent: function(){
        this.addEvents(
            'rowdblclick',
            'select'
        );
        Ext.apply(this, {
        	items:[
        		Ext.widget('panel',{
					layout: 'anchor', 
					border:true,
					items:[
						Ext.widget('gridpanel',{
			        		columnLines : false,
			        		id:'curnotifygridpanel',
			    			autoScroll : true,
			    			anchor:'100% 100%',
			    			layout:'fit',
               				store: Ext.create('Ext.data.Store', {
                   				fields:['CN_ID','CN_DESC','CN_MAN','CN_DATE','CN_KEYFIELD','CN_KEYVALUE','CN_URL'],
                				data: []
		         			 }),
			                columns: [{
			                    text: '标题',
			                    dataIndex: 'CN_DESC',
			                    cls:'x-grid-header-simple',
			                    flex: 1,
			                    sortable:false,
			                    renderer:function(val,meta,record){
			                    	var url=record.get('CN_URL');
			                    	if(contains(url, '?', true)){
										url= url + '&formCondition=' + record.get('CN_KEYFIELD')+'IS'+record.get('CN_KEYVALUE');
									} else {
										url= url + '?&formCondition=' + record.get('CN_KEYFIELD')+'IS'+record.get('CN_KEYVALUE');
									}
									return Ext.String.format('<span style="color:#436EEE;padding-left:2px;"><a class="x-btn-link" onclick="openTable(\''+val+'\',\''+url+'\',null);" target="_blank"  style="padding-left:2px">{0}&nbsp;</a></span>',
												val
										);
			                    }
			                },{
								text:'发送人',
								cls:'x-grid-header-simple',
								width:100,
								dataIndex:'CN_MAN',
								renderer:function(value){
									return value;
								}
							},{
								text: '发送时间',
			                    cls:'x-grid-header-simple',
			                    xtype:'datecolumn',
			                    dataIndex: 'CN_DATE',
			                    width: 200,
			                    renderer:function(value){
									return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
								}
               				 }]
        				})]
         		  })]
        	});
        this.getData();
        this.callParent(arguments);
    },
	getData:function(){
		var con='cn_enuu='+enUU+' and cn_emuu='+emUU;
		var me=this;
		Ext.Ajax.request({
	    		   url : basePath + 'opensys/getCurNotify.action',
	    		   async: false,
	    		   params: {
	    			   condition:con
	    		   },
	    		   method : 'get',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.success && r.data){
	    				  Ext.getCmp('curnotifygridpanel').store.loadData(r.data);
	    			   }
	    		   }
	    });
	
	},
    onRowDblClick: function(view, record, item, index, e) {
        this.fireEvent('rowdblclick', this, this.store.getAt(index));
    },

    onSelect: function(model, selections){
        var selected = selections[0];
        if (selected) {
            this.fireEvent('select', this, selected);
        }
    },
    onLoad: function(store, records, success) {
        if (this.getStore().getCount()) {
            this.getSelectionModel().select(0);
        }
    },
    onProxyException: function(proxy, response, operation) {
        Ext.Msg.alert("Error with data from server", operation.error);
        this.view.el.update('');
        
        // Update the detail view with a dummy empty record
        this.fireEvent('select', this, {data:{}});
    },
    loadFeed: function(url){
        var store = this.store;
        store.getProxy().extraParams.feed = url;
        store.load();
    },

    formatTitle: function(value, p, record){
        return Ext.String.format('<div class="topic"><b>{0}</b><span class="author">{1}</span></div>', value, record.get('author') || "Unknown");
    },
    formatDate: function(date){
        if (!date) {
            return '';
        }

        var now = new Date(), d = Ext.Date.clearTime(now, true), notime = Ext.Date.clearTime(date, true).getTime();

        if (notime === d.getTime()) {
            return 'Today ' + Ext.Date.format(date, 'g:i a');
        }

        d = Ext.Date.add(d, 'd', -6);
        if (d.getTime() <= notime) {
            return Ext.Date.format(date, 'D g:i a');
        }
        return Ext.Date.format(date, 'Y/m/d g:i a');
    }
});
