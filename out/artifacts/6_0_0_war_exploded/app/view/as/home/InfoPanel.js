Ext.define('erp.view.opensys.home.InfoPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.infopanel',
    title:'消息中心',
    region: 'center',
    border: false,
   /* split: true,*/
    flex: 2,
    layout:'fit',
    initComponent: function(){
        this.addEvents(
            'rowdblclick',
            'select'
        );
        Ext.apply(this, {
        	items:[{
        		xtype:'grid',
        		border:true,
                store: Ext.create('Ext.data.Store', {
                    fields:['pr_id','pr_context','pr_date'],
                    sortInfo: {
                        property: 'pr_date',
                        direction: 'DESC'
                    }, 
                    proxy: {
                        type: 'ajax',
                        url: '',
                        reader: {
                            type: 'json',
                            record: 'data'
                        },
                        listeners: {
                            exception: this.onProxyException,
                            scope: this
                        }
                    },
                    listeners: {
                        load: this.onLoad,
                        scope: this
                    }
                }),
                columns: [{
                    text: '消息内容',
                    dataIndex: 'pr_context',
                    flex: 1,
                    renderer: this.formatTitle,
                    sortable:false
                }, {
                    text: '发送时间',
                    dataIndex: 'pr_date',
                    renderer: this.formatDate,
                    width: 200
                }]
        		
        		
        	}]
            
        });
        this.callParent(arguments);
        this.on('selectionchange', this.onSelect, this);
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
