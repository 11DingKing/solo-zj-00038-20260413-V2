import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-stat-card-skeleton',
    templateUrl: './stat-card-skeleton.component.html',
    styles: [`
        .skeleton-card {
            background: #fff;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px rgba(0, 0, 0, 0.02);
        }

        .skeleton-content {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .skeleton-text-section {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .skeleton-number {
            width: 80px;
            height: 32px;
            background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 37%, #f5f5f5 63%);
            background-size: 400% 100%;
            animation: pulse 1.5s ease-in-out infinite;
            border-radius: 4px;
        }

        .skeleton-label {
            width: 120px;
            height: 14px;
            background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 37%, #f5f5f5 63%);
            background-size: 400% 100%;
            animation: pulse 1.5s ease-in-out infinite;
            animation-delay: 0.1s;
            border-radius: 4px;
        }

        .skeleton-icon {
            width: 48px;
            height: 48px;
            background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 37%, #f5f5f5 63%);
            background-size: 400% 100%;
            animation: pulse 1.5s ease-in-out infinite;
            animation-delay: 0.2s;
            border-radius: 50%;
        }

        @keyframes pulse {
            0% {
                background-position: 100% 50%;
            }
            100% {
                background-position: 0 50%;
            }
        }
    `]
})
export class StatCardSkeletonComponent {
    @Input() rows: number = 1;

    getItems(): number[] {
        return Array.from({length: this.rows}, (_, i) => i);
    }
}