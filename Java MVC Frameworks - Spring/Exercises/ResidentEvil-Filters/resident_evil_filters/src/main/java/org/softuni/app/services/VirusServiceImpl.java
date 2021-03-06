package org.softuni.app.services;

import org.modelmapper.ModelMapper;
import org.softuni.app.models.dto.VirusCreateDto;
import org.softuni.app.models.dto.VirusShowDto;
import org.softuni.app.models.entities.Capital;
import org.softuni.app.models.entities.Virus;
import org.softuni.app.models.entities.enums.MagnitudeType;
import org.softuni.app.models.entities.enums.MutationType;
import org.softuni.app.repositories.CapitalRepository;
import org.softuni.app.repositories.VirusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class VirusServiceImpl implements VirusService {

    private VirusRepository virusRepository;

    private CapitalRepository capitalRepository;

    private ModelMapper modelMapper;

    public VirusServiceImpl(VirusRepository virusRepository, CapitalRepository capitalRepository, ModelMapper modelMapper) {
        this.virusRepository = virusRepository;
        this.capitalRepository = capitalRepository;
        this.modelMapper = modelMapper;
    }

    private void mapCreateVirusDtoToVirusEntity(VirusCreateDto virusCreateDto, Virus editedVirus) {
        editedVirus.setName(virusCreateDto.getName());
        editedVirus.setDescription(virusCreateDto.getDescription());
        editedVirus.setSideEffects(virusCreateDto.getSideEffects());
        editedVirus.setCreator(virusCreateDto.getCreator());
        editedVirus.setDeadly(virusCreateDto.isDeadly());
        editedVirus.setCurable(virusCreateDto.isCurable());
        editedVirus.setMutation(MutationType.valueOf(virusCreateDto.getMutation().toUpperCase()));
        editedVirus.setTurnoverRate(virusCreateDto.getTurnoverRate());
        editedVirus.setHoursUntilTurn(virusCreateDto.getHoursUntilTurn());
        editedVirus.setMagnitude(MagnitudeType.valueOf(virusCreateDto.getMagnitude().toUpperCase()));
        editedVirus.setReleasedOn(LocalDate.parse(virusCreateDto.getReleasedOn()));

        Set<Capital> capitals = new HashSet<>();

        for (String capitalName : virusCreateDto.getCapitals()) {
            List<Capital> capitalsByName = this.capitalRepository.findByName(capitalName);
            capitals.addAll(capitalsByName);
        }

        editedVirus.setCapitals(capitals);
    }

    @Override
    public boolean create(VirusCreateDto virusCreateDto) {
        Virus virus = new Virus();
        this.mapCreateVirusDtoToVirusEntity(virusCreateDto, virus);

        return this.virusRepository.save(virus) != null;
    }

    @Override
    public Set<VirusShowDto> findAll() {
        List<Virus> viruses = this.virusRepository.findAll();
        Set<VirusShowDto> virusShowDtos = new LinkedHashSet<>();

        for (Virus virus : viruses) {
            virusShowDtos.add(this.modelMapper.map(virus, VirusShowDto.class));
        }

        return virusShowDtos;
    }

    @Override
    public VirusCreateDto findById(String id) {
        Optional<Virus> virusOptional = this.virusRepository.findById(id);

        Virus virus = null;
        if (virusOptional.isPresent()) {
            virus = virusOptional.get();
        }

        VirusCreateDto virusCreateDto = null;

        if (virus != null) {
            virusCreateDto = this.modelMapper.map(virus, VirusCreateDto.class);
        }

        return virusCreateDto;
    }

    @Override
    public boolean edit(VirusCreateDto virusCreateDto, String id) {
        Virus editedVirus = this.virusRepository.findById(id).get();

        this.mapCreateVirusDtoToVirusEntity(virusCreateDto, editedVirus);

        return this.virusRepository.save(editedVirus) != null;
    }

    @Override
    public void delete(String id) {
        this.virusRepository.deleteById(id);
    }
}
