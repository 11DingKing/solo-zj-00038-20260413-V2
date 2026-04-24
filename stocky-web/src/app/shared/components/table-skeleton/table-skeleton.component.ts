import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-table-skeleton',
    templateUrl: './table-skeleton.component.html',
    styles: [`
        .skeleton-table-wrapper {
            background: #fff;
            border-radius: 8px;
            padding: 24px;
        }

        .skeleton-header {
            display: flex;
            gap: 16px;
            margin-bottom: 24px;
        }

        .skeleton-header-item {
            width: 120px;
            height: 32px;
            background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 37%, #f5f5f5 63%);
            background-size: 400% 100%;
            animation: pulse 1.5s ease-in-out infinite;
            border-radius: 4px;
        }

        .skeleton-table {
            width: 100%;
            border-collapse: collapse;
        }

        .skeleton-table th,
        .skeleton-table td {
            padding: 16px;
            border-bottom: 1px solid #f0f0f0;
        }

        .skeleton-cell {
            height: 16px;
            background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 37%, #f5f5f5 63%);
            background-size: 400% 100%;
            animation: pulse 1.5s ease-in-out infinite;
            border-radius: 4px;
        }

        .skeleton-checkbox {
            width: 16px;
            height: 16px;
        }

        .skeleton-header-cell {
            height: 14px;
            background: #f5f5f5;
            border-radius: 2px;
        }

        @keyframes pulse {
            0% {
                background-position: 100% 50%;
            }
            100% {
                background-position: 0 50%;
            }
        }

        .skeleton-row:nth-child(1) .skeleton-cell { animation-delay: 0s; }
        .skeleton-row:nth-child(2) .skeleton-cell { animation-delay: 0.1s; }
        .skeleton-row:nth-child(3) .skeleton-cell { animation-delay: 0.2s; }
        .skeleton-row:nth-child(4) .skeleton-cell { animation-delay: 0.3s; }
        .skeleton-row:nth-child(5) .skeleton-cell { animation-delay: 0.4s; }
    `]
})
export class TableSkeletonComponent {
    @Input() columns: number = 6;
    @Input() rows: number = 5;

    getColumns(): number[] {
        return Array.from({length: this.columns}, (_, i) => i);
    }

    getRows(): number[] {
        return Array.from({length: this.rows}, (_, i) => i);
    }
}