Ext.define('erp.view.plm.resource.BarChart',{ 
	extend: 'Ext.chart.Chart', 
	alias: 'widget.barChart',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	id:'barchart',
	flex: 1,
    shadow: true,
    animate: true,
    store: store,
    bodyStyle:{
               background: '#F5F5DC'
            },
    axes: [{
            type: 'Numeric',
            position: 'left',
            fields: ['percentdone'],
            minimum: 0,
            maximum:100,
            label: {
                    renderer: Ext.util.Format.numberRenderer('0,0')
                },
            grid: true,
            title: '综合分析',
        }, {
            type: 'Category',
            position: 'bottom',
            fields: ['name'],
            label: {
                renderer: function(v) {
                    return Ext.String.ellipsis(v, 15, false);
                },
                font: '9px Arial',
                rotate: {
                    degrees: 270
                }
            }
      }],
       series: [{
            type: 'column',
            axis: 'left',
            highlight: true,
            maxWidth :20,
           /** style: {
                fill: '#456d9f'
            },**/
            highlightCfg: {
                fill: '#a2b5ca'
            },
            /**label: {
                contrast: true,
                display: 'insideEnd',
                field: 'percentdone',
                color: '#000',
                orientation: 'vertical',
                'text-anchor': 'middle'
            },**/
            listeners: {
                'itemmouseup': function(item) {
                     var series = barChart.series.get(0),
                         index = Ext.Array.indexOf(series.items, item),
                         selectionModel = gridPanel.getSelectionModel();
                     
                     selectedStoreItem = item.storeItem;
                     selectionModel.select(index);
                }
            },
            xField: 'name',
            yField: ['percentdone'],
           renderer: function(sprite, record, attr, index, store) {
                    var fieldValue = Math.random() * 20 + 10;
                    var value = (record.get('count') >> 0) % 5;
                    var color = ['rgb(213, 70, 121)', 
                                 'rgb(44, 153, 201)', 
                                 'rgb(146, 6, 157)', 
                                 'rgb(49, 149, 0)', 
                                 'rgb(249, 153, 0)'][value];
                    return Ext.apply(attr, {
                        fill: color,
                        style:{
                         maxWidth :20,
                        }
                    });
                }
        }]        
    });