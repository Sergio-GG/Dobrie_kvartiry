import { Component, ElementRef, HostListener, inject, input, OnInit, output, signal } from '@angular/core';
import { Field, form} from '@angular/forms/signals';
import { Task } from '../models/task.model';
import { FormsModule, Validators } from '@angular/forms';


export type TaskStatus = 'NEW' | 'IN_PROGRESS' | 'DONE' | 'CANCELLED';

@Component({
  selector: 'app-modal-form',
  imports: [FormsModule],
  templateUrl: './modal-form.html',
  styleUrl: './modal-form.css',
})
export class ModalForm {
  
  public taskModel = signal<Task> ({
    title: '',
    description: '',
    status: 'NEW',
    createdAt: new Date().toLocaleDateString(),
    updatedAt: new Date().toLocaleDateString()
  })

  protected taskForm = form(this.taskModel);

  public close = output<void>();

  public save = output<Task>(); 

  public elementRef = inject(ElementRef);

  onClose() {
    this.close.emit();
  }

  @HostListener('click', ['$event'])
  onClickClose(event: MouseEvent) {
    if (event.target === this.elementRef.nativeElement) {
      this.close.emit();
    }
  }

  onSave() {
    const newTask = this.taskModel(); 
    this.save.emit(newTask);
  }

  updateField(key: keyof Task, value: any) {
    this.taskModel.update(task => ({ ...task, [key]: value }));
  }
}

  
  


